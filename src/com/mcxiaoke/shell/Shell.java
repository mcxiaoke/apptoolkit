/*
 * Copyright (C) 2012-2013 Jorrit "Chainfire" Jongma
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.shell;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.shell.utils.Remounter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class providing functionality to execute commands in a (root) shell
 */
public class Shell {
    private static final String TAG = Shell.class.getSimpleName();

    private static boolean sDebug;

    public static boolean isDebug() {
        return sDebug;
    }

    public static void enable() {
        sDebug = true;
    }

    public static void disable() {
        sDebug = false;
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    public static void log(String message) {
        if (sDebug) {
            Log.v(TAG, message);
        }
    }

    public static void log(String... messages) {
        if (sDebug) {
            if (messages != null && messages.length > 0) {
                for (String message : messages) {
                    Log.v(TAG, message);
                }
            }
        }
    }

    public static void error(Throwable throwable) {
        if (sDebug) {
            Log.e(TAG, "error: " + throwable);
        }
    }

    public static void error(String message) {
        if (sDebug) {
            Log.e(TAG, "message: " + message);
        }
    }

    public static void error(Throwable throwable, String message) {
        if (sDebug) {
            Log.e(TAG, "error: " + throwable + " message: " + message);
        }
    }


    public static final String[] SUPERUSER_PACKAGE = new String[]{
            "eu.chainfire.supersu", "eu.chainfire.supersu.pro",
            "com.noshufou.android.su", "com.miui.uac",
            "com.lbe.security.shuame", "com.lbe.security.miui", "com.m0narx.su"};

    /**
     * The set of su location I know by now.
     */
    public static final String[] SU_BINARY_DIRS = {
            "/system/bin", "/system/sbin", "/system/xbin",
            "/vendor/bin", "/sbin"
    };


    /**
     * Check if command need patch.
     *
     * @return
     */
    public static boolean isNeedLibPath() {
        return android.os.Build.VERSION.SDK_INT == 17;
    }

    /**
     * <p>Runs commands using the supplied shell, and returns the output, or null in
     * case of errors.</p>
     * <p/>
     * <p>Note that due to compatibility with older Android versions,
     * wantSTDERR is not implemented using redirectErrorStream, but rather appended
     * to the output. STDOUT and STDERR are thus not guaranteed to be in the correct
     * order in the output.</p>
     * <p/>
     * <p>Note as well that this code will intentionally crash when run in debug mode
     * from the main thread of the application. You should always execute shell
     * commands from a background thread.</p>
     * <p/>
     * <p>When in debug mode, the code will also excessively log the commands passed to
     * and the output returned from the shell.</p>
     * <p/>
     * <p>Though this function uses background threads to gobble STDOUT and STDERR so
     * a deadlock does not occur if the shell produces massive output, the output is
     * still stored in a List&lt;String&gt;, and as such doing something like <em>'ls -lR /'</em>
     * will probably have you run out of memory.</p>
     *
     * @param shell       The shell to use for executing the commands
     * @param commands    The commands to execute
     * @param environment List of all environment variables (in 'key=value' format) or null for defaults
     * @param wantSTDERR  Return STDERR in the output ?
     * @return Output of the commands, or null in case of an error
     */
    private static List<String> run(String shell, String[] commands, String[] environment, boolean wantSTDERR) {
        String shellUpper = shell.toUpperCase();

        if (isDebug()) {
            // check if we're running in the main thread, and if so, crash if we're in debug mode,
            // to let the developer know attention is needed here.

            if ((Looper.myLooper() != null) && (Looper.myLooper() == Looper.getMainLooper())) {
                log(ShellOnMainThreadException.EXCEPTION_COMMAND);
                throw new ShellOnMainThreadException(ShellOnMainThreadException.EXCEPTION_COMMAND);
            }

            log(String.format("[%s%%] START", shellUpper));
        }

        List<String> res = Collections.synchronizedList(new ArrayList<String>());

        try {
            // Combine passed environment with system environment
            if (environment != null) {
                Map<String, String> newEnvironment = new HashMap<String, String>();
                newEnvironment.putAll(System.getenv());
                int split;
                for (String entry : environment) {
                    if ((split = entry.indexOf("=")) >= 0) {
                        newEnvironment.put(entry.substring(0, split), entry.substring(split + 1));
                    }
                }
                int i = 0;
                environment = new String[newEnvironment.size()];
                for (Map.Entry<String, String> entry : newEnvironment.entrySet()) {
                    environment[i] = entry.getKey() + "=" + entry.getValue();
                    i++;
                }
            }

            // setup our process, retrieve STDIN stream, and STDOUT/STDERR gobblers
            Process process = Runtime.getRuntime().exec(shell, environment);
            DataOutputStream STDIN = new DataOutputStream(process.getOutputStream());
            StreamGobbler STDOUT = new StreamGobbler(shellUpper + "-", process.getInputStream(), res);
            StreamGobbler STDERR = new StreamGobbler(shellUpper + "*", process.getErrorStream(), wantSTDERR ? res : null);

            // start gobbling and write our commands to the shell
            STDOUT.start();
            STDERR.start();
            for (String write : commands) {
                if (isDebug()) log(String.format("[%s+] %s", shellUpper, write));
                STDIN.writeBytes(write + "\n");
                STDIN.flush();
            }
            STDIN.writeBytes("exit\n");
            STDIN.flush();

            // wait for our process to finish, while we gobble away in the background
            process.waitFor();

            // make sure our threads are done gobbling, our streams are closed, and the process is
            // destroyed - while the latter two shouldn't be needed in theory, and may even produce
            // warnings, in "normal" Java they are required for guaranteed cleanup of resources, so
            // lets be safe and do this on Android as well
            try {
                STDIN.close();
            } catch (IOException e) {
            }
            STDOUT.join();
            STDERR.join();
            process.destroy();

            // in case of su, 255 usually indicates access denied
            if (shell.equals("su") && (process.exitValue() == 255)) {
                res = null;
            }
        } catch (IOException e) {
            if (isDebug()) {
                error(e);
            }
            // shell probably not found
            res = null;
        } catch (InterruptedException e) {
            // this should really be re-thrown
            if (isDebug()) {
                error(e);
            }
            res = null;
        } catch (Exception e) {
            if (isDebug()) {
                error(e);
            }
            res = null;
        }

        if (isDebug()) log(String.format("[%s%%] END", shell.toUpperCase()));
        return res;
    }

    /**
     * This class provides utility functions to easily execute commands using SH
     */
    public static class SH {
        /**
         * Runs command and return output
         *
         * @param command The command to run
         * @return Output of the command, or null in case of an error
         */
        public static List<String> run(String command) {
            return Shell.run("sh", new String[]{command}, null, false);
        }

        /**
         * Runs commands and return output
         *
         * @param commands The commands to run
         * @return Output of the commands, or null in case of an error
         */
        public static List<String> run(List<String> commands) {
            return Shell.run("sh", commands.toArray(new String[commands.size()]), null, false);
        }

        /**
         * Runs commands and return output
         *
         * @param commands The commands to run
         * @return Output of the commands, or null in case of an error
         */
        public static List<String> run(String[] commands) {
            return Shell.run("sh", commands, null, false);
        }
    }

    /**
     * This class provides utility functions to easily execute commands using SU
     * (root shell), as well as detecting whether or not root is available, and
     * if so which version.
     */
    public static class SU {
        /**
         * Runs command as root (if available) and return output
         *
         * @param command The command to run
         * @return Output of the command, or null if root isn't available or in case of an error
         */
        public static List<String> run(String command) {
            return Shell.run("su", new String[]{command}, null, false);
        }

        /**
         * Runs commands as root (if available) and return output
         *
         * @param commands The commands to run
         * @return Output of the commands, or null if root isn't available or in case of an error
         */
        public static List<String> run(List<String> commands) {
            return Shell.run("su", commands.toArray(new String[commands.size()]), null, false);
        }

        /**
         * Runs commands as root (if available) and return output
         *
         * @param commands The commands to run
         * @return Output of the commands, or null if root isn't available or in case of an error
         */
        public static List<String> run(String[] commands) {
            return Shell.run("su", commands, null, false);
        }

        /**
         * Detects whether or not superuser access is available, by checking the output
         * of the "id" command if available, checking if a shell runs at all otherwise
         *
         * @return True if superuser access available
         */
        public static boolean available() {
            // this is only one of many ways this can be done

            List<String> ret = run(new String[]{
                    "echo -BOC-",
                    "id"
            });
            if (ret == null) return false;

            boolean echo_seen = false;

            for (String line : ret) {
                if (line.contains("uid=")) {
                    // id command is working, let's see if we are actually root
                    return line.contains("uid=0");
                } else if (line.contains("-BOC-")) {
                    // if we end up here, at least the su command starts some kind of shell,
                    // let's hope it has root privileges - no way to know without additional
                    // native binaries
                    echo_seen = true;
                }
            }

            return echo_seen;
        }

        /**
         * <p>Detects the version of the su binary installed (if any), if supported by the binary.
         * Most binaries support two different version numbers, the public version that is
         * displayed to users, and an internal version number that is used for version number
         * comparisons. Returns null if su not available or retrieving the version isn't supported.</p>
         * <p/>
         * <p>Note that su binary version and GUI (APK) version can be completely different.</p>
         *
         * @param internal Request human-readable version or application internal version
         * @return String containing the su version or null
         */
        public static String version(boolean internal) {
            // we add an additional exit call, because the command
            // line options are not available in all su versions,
            // thus potentially launching a shell instead

            List<String> ret = Shell.run("sh", new String[]{
                    internal ? "su -V" : "su -v",
                    "exit"
            }, null, false);
            if (ret == null) return null;

            for (String line : ret) {
                if (!internal) {
                    if (line.contains(".")) return line;
                } else {
                    try {
                        if (Integer.parseInt(line) > 0) return line;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            return null;
        }
    }

    /**
     * Command result callback, notifies the recipient of the completion of a command
     * block, including the (last) exit code, and the full output
     */
    public interface OnCommandResultListener {
        /**
         * <p>Command result callback</p>
         * <p/>
         * <p>Depending on how and on which thread the shell was created, this callback
         * may be executed on one of the gobbler threads. In that case, it is important
         * the callback returns as quickly as possible, as delays in this callback may
         * pause the native process or even result in a deadlock</p>
         * <p/>
         * <p>See {@link Shell.Interactive} for threading details</p>
         *
         * @param commandCode Value previously supplied to addCommand
         * @param exitCode    Exit code of the last command in the block
         * @param output      All output generated by the command block
         */
        public void onCommandResult(int commandCode, int exitCode, List<String> output);
    }

    /**
     * Internal class to store command block proprties
     */
    private static class Command {
        private static int commandCounter = 0;

        private final String[] commands;
        private final int code;
        private final OnCommandResultListener onCommandResultListener;
        private final String marker;

        public Command(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            this.commands = commands;
            this.code = code;
            this.onCommandResultListener = onCommandResultListener;
            this.marker = UUID.randomUUID().toString() + String.format("-%08x", ++commandCounter);
        }
    }

    /**
     * Builder class for {@link Shell.Interactive}
     */
    public static class Builder {
        private Handler handler = null;
        private boolean autoHandler = true;
        private String shell = "sh";
        private boolean wantSTDERR = false;
        private List<Command> commands = new LinkedList<Command>();
        private Map<String, String> environment = new HashMap<String, String>();
        private StreamGobbler.OutputCallback standinCallback = null;
        private StreamGobbler.OutputCallback standoutCallback = null;

        /**
         * <p>Set a custom handler that will be used to post all callbacks to</p>
         * <p/>
         * <p>See {@link Shell.Interactive} for further details on threading and handlers</p>
         *
         * @param handler Handler to use
         * @return This Builder object for method chaining
         */
        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        /**
         * <p>Automatically create a handler if possible ? Default to true</p>
         * <p/>
         * <p>See {@link Shell.Interactive} for further details on threading and handlers</p>
         *
         * @param autoHandler Auto-create handler ?
         * @return This Builder object for method chaining
         */
        public Builder setAutoHandler(boolean autoHandler) {
            this.autoHandler = autoHandler;
            return this;
        }

        /**
         * Set shell binary to use. Usually "sh" or "su", do not use a full path
         * unless you have a good reason to
         *
         * @param shell Shell to use
         * @return This Builder object for method chaining
         */
        public Builder setShell(String shell) {
            this.shell = shell;
            return this;
        }

        /**
         * Convenience function to set "sh" as used shell
         *
         * @return This Builder object for method chaining
         */
        public Builder useSH() {
            return setShell("sh");
        }

        /**
         * Convenience function to set "su" as used shell
         *
         * @return This Builder object for method chaining
         */
        public Builder useSU() {
            return setShell("su");
        }

        /**
         * Set if error output should be appended to command block result output
         *
         * @param wantSTDERR Want error output ?
         * @return This Builder object for method chaining
         */
        public Builder setWantSTDERR(boolean wantSTDERR) {
            this.wantSTDERR = wantSTDERR;
            return this;
        }

        /**
         * Add or update an environment variable
         *
         * @param key   Key of the environment variable
         * @param value Value of the environment variable
         * @return This Builder object for method chaining
         */
        public Builder addEnvironment(String key, String value) {
            environment.put(key, value);
            return this;
        }

        /**
         * Add or update environment variables
         *
         * @param addEnvironment Map of environment variables
         * @return This Builder object for method chaining
         */
        public Builder addEnvironment(Map<String, String> addEnvironment) {
            environment.putAll(addEnvironment);
            return this;
        }

        /**
         * Add a command to execute
         *
         * @param command Command to execute
         * @return This Builder object for method chaining
         */
        public Builder addCommand(String command) {
            return addCommand(command, 0, null);
        }

        /**
         * <p>Add a command to execute, with a callback to be called on completion</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param command                 Command to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion
         * @return This Builder object for method chaining
         */
        public Builder addCommand(String command, int code, OnCommandResultListener onCommandResultListener) {
            return addCommand(new String[]{command}, code, onCommandResultListener);
        }

        /**
         * Add commands to execute
         *
         * @param commands Commands to execute
         * @return This Builder object for method chaining
         */
        public Builder addCommand(List<String> commands) {
            return addCommand(commands, 0, null);
        }

        /**
         * <p>Add commands to execute, with a callback to be called on completion (of all commands)</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param commands                Commands to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion (of all commands)
         * @return This Builder object for method chaining
         */
        public Builder addCommand(List<String> commands, int code, OnCommandResultListener onCommandResultListener) {
            return addCommand(commands.toArray(new String[commands.size()]), code, onCommandResultListener);
        }

        /**
         * Add commands to execute
         *
         * @param commands Commands to execute
         * @return This Builder object for method chaining
         */
        public Builder addCommand(String[] commands) {
            return addCommand(commands, 0, null);
        }

        /**
         * <p>Add commands to execute, with a callback to be called on completion (of all commands)</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param commands                Commands to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion (of all commands)
         * @return This Builder object for method chaining
         */
        public Builder addCommand(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            this.commands.add(new Command(commands, code, onCommandResultListener));
            return this;
        }

        /**
         * <p>Set a callback called for every line output to STDOUT by the shell</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param outputCallback Callback to be called for each line
         * @return This Builder object for method chaining
         */
        public Builder setStandinCallback(StreamGobbler.OutputCallback outputCallback) {
            this.standinCallback = outputCallback;
            return this;
        }

        /**
         * <p>Set a callback called for every line output to STDERR by the shell</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param outputCallback Callback to be called for each line
         * @return This Builder object for method chaining
         */
        public Builder setStandoutCallback(StreamGobbler.OutputCallback outputCallback) {
            this.standoutCallback = outputCallback;
            return this;
        }

        /**
         * Construct a {@link Shell.Interactive} instance, and start the shell
         */
        public Interactive open() {
            return new Interactive(this);
        }
    }

    /**
     * <p>An interactive shell - initially created with {@link Shell.Builder} - that
     * executes blocks of commands you supply in the background, optionally calling
     * callbacks as each block completes.</p>
     * <p/>
     * <p>STDERR output can be supplied as well, but due to compatibility with older
     * Android versions, wantSTDERR is not implemented using redirectErrorStream,
     * but rather appended to the output. STDOUT and STDERR are thus not guaranteed to
     * be in the correct order in the output.</p>
     * <p/>
     * <p>Note as well that this code will intentionally crash when run in debug mode
     * from the main thread of the application. You should always execute shell
     * commands from a background thread.</p>
     * <p/>
     * <p>When in debug mode, the code will also excessively log the commands passed to
     * and the output returned from the shell.</p>
     * <p/>
     * <p>Though this function uses background threads to gobble STDOUT and STDERR so
     * a deadlock does not occur if the shell produces massive output, the output is
     * still stored in a List&lt;String&gt;, and as such doing something like <em>'ls -lR /'</em>
     * will probably have you run out of memory when using a
     * {@link Shell.OnCommandResultListener}. A work-around is to not supply this callback,
     * but using (only) {@link Shell.Builder#setOnSTDOUTLineListener(com.libsuperuser.StreamGobbler.OutputCallback)}. This
     * way, an internal buffer will not be created and wasting your memory.</p>
     * <p/>
     * <h3>Callbacks, threads and handlers</h3>
     * <p/>
     * <p>On which thread the callbacks execute is dependent on your initialization. You can
     * supply a custom Handler using {@link Shell.Builder#setHandler(android.os.Handler)} if needed.
     * If you do not supply a custom Handler - unless you set {@link Shell.Builder#setAutoHandler(boolean)}
     * to false - a Handler will be auto-created if the thread used for instantiation
     * of the object has a Looper.</p>
     * <p/>
     * <p>If no Handler was supplied and it was also not auto-created, all callbacks will
     * be called from either the STDOUT or STDERR gobbler threads. These are important
     * threads that should be blocked as little as possible, as blocking them may in rare
     * cases pause the native process or even create a deadlock.</p>
     * <p/>
     * <p>The main thread must certainly has a Looper, thus if you call {@link Shell.Builder#open()}
     * from the main thread, a handler will (by default) be auto-created, and all the callbacks
     * will be called on the main thread. While this is often convenient and easy to code with,
     * you should be aware that if your callbacks are 'expensive' to execute, this may negatively
     * impact UI performance.</p>
     * <p/>
     * <p>Background threads usually do <em>not</em> have a Looper, so calling {@link Shell.Builder#open()}
     * from such a background thread will (by default) result in all the callbacks being executed
     * in one of the gobbler threads. You will have to make sure the code you execute in these callbacks
     * is thread-safe.</p>
     */
    public static class Interactive {
        private final Handler handler;
        private final boolean autoHandler;
        private final String shell;
        private final boolean wantSTDERR;
        private final List<Command> commands;
        private final Map<String, String> environment;
        private final StreamGobbler.OutputCallback onSTDOUTLineListener;
        private final StreamGobbler.OutputCallback onSTDERRLineListener;

        private Process process = null;
        private DataOutputStream STDIN = null;
        private StreamGobbler STDOUT = null;
        private StreamGobbler STDERR = null;

        private volatile boolean running = false;
        private volatile boolean idle = true; // read/write only synchronized
        private volatile boolean closed = true;
        private volatile int callbacks = 0;

        private Object idleSync = new Object();
        private Object callbackSync = new Object();

        private volatile int lastExitCode = 0;
        private volatile String lastMarkerSTDOUT = null;
        private volatile String lastMarkerSTDERR = null;
        private volatile Command command = null;
        private volatile List<String> buffer = null;

        /**
         * The only way to create an instance: Shell.Builder::open()
         *
         * @param builder Builder class to take values from
         */
        private Interactive(Builder builder) {
            autoHandler = builder.autoHandler;
            shell = builder.shell;
            wantSTDERR = builder.wantSTDERR;
            commands = builder.commands;
            environment = builder.environment;
            onSTDOUTLineListener = builder.standinCallback;
            onSTDERRLineListener = builder.standoutCallback;

            // If a looper is available, we offload the callbacks from the gobbling threads
            // to whichever thread created us. Would normally do this in open(),
            // but then we could not declare handler as final
            if ((Looper.myLooper() != null) && (builder.handler == null) && autoHandler) {
                handler = new Handler();
            } else {
                handler = builder.handler;
            }

            open();
        }

        @Override
        protected void finalize() throws Throwable {
            if (!closed && isDebug()) {
                // waste of resources
                log(ShellNotClosedException.EXCEPTION_NOT_CLOSED);
                throw new ShellNotClosedException();
            }
            super.finalize();
        }

        /**
         * Add a command to execute
         *
         * @param command Command to execute
         */
        public void addCommand(String command) {
            addCommand(command, 0, null);
        }

        /**
         * <p>Add a command to execute, with a callback to be called on completion</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param command                 Command to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion
         */
        public void addCommand(String command, int code, OnCommandResultListener onCommandResultListener) {
            addCommand(new String[]{command}, code, onCommandResultListener);
        }

        /**
         * Add commands to execute
         *
         * @param commands Commands to execute
         */
        public void addCommand(List<String> commands) {
            addCommand(commands, 0, null);
        }

        /**
         * <p>Add commands to execute, with a callback to be called on completion (of all commands)</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param commands                Commands to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion (of all commands)
         */
        public void addCommand(List<String> commands, int code, OnCommandResultListener onCommandResultListener) {
            addCommand(commands.toArray(new String[commands.size()]), code, onCommandResultListener);
        }

        /**
         * Add commands to execute
         *
         * @param commands Commands to execute
         */
        public void addCommand(String[] commands) {
            addCommand(commands, 0, null);
        }

        /**
         * <p>Add commands to execute, with a callback to be called on completion (of all commands)</p>
         * <p/>
         * <p>The thread on which the callback executes is dependent on various factors, see {@link Shell.Interactive} for further details</p>
         *
         * @param commands                Commands to execute
         * @param code                    User-defined value passed back to the callback
         * @param onCommandResultListener Callback to be called on completion (of all commands)
         */
        public synchronized void addCommand(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            if (running) {
                this.commands.add(new Command(commands, code, onCommandResultListener));
                runNextCommand();
            }
        }

        /**
         * Run the next command if any and if ready, signals idle state if no commands left
         */
        private void runNextCommand() {
            runNextCommand(true);
        }

        /**
         * Run the next command if any and if ready
         *
         * @param notifyIdle signals idle state if no commands left ?
         */
        private void runNextCommand(boolean notifyIdle) {
            // must always be called from a synchronized method

            boolean running = isRunning();
            if (!running) idle = true;

            if (running && idle && (commands.size() > 0)) {
                Command command = commands.get(0);
                commands.remove(0);

                buffer = null;
                lastExitCode = 0;
                lastMarkerSTDOUT = null;
                lastMarkerSTDERR = null;

                if (command.commands.length > 0) {
                    try {
                        if (command.onCommandResultListener != null) {
                            // no reason to store the output if we don't have an OnCommandResultListener
                            // user should catch the output with an OnLineListener in this case
                            buffer = Collections.synchronizedList(new ArrayList<String>());
                        }

                        idle = false;
                        this.command = command;
                        for (String write : command.commands) {
                            if (isDebug()) log(String.format("[%s+] %s", shell.toUpperCase(), write));
                            STDIN.writeBytes(write + "\n");
                        }
                        STDIN.writeBytes("echo " + command.marker + " $?\n");
                        STDIN.writeBytes("echo " + command.marker + " >&2\n");
                        STDIN.flush();
                    } catch (IOException e) {
                    }
                } else {
                    runNextCommand(false);
                }
            }

            if (idle && notifyIdle) {
                synchronized (idleSync) {
                    idleSync.notifyAll();
                }
            }
        }

        /**
         * Processes a STDOUT/STDERR line containing an end/exitCode marker
         */
        private synchronized void processMarker() {
            if (command.marker.equals(lastMarkerSTDOUT) && (command.marker.equals(lastMarkerSTDERR))) {
                if (command.onCommandResultListener != null) {
                    if (buffer != null) {
                        if (handler != null) {
                            final List<String> fBuffer = buffer;
                            final int fExitCode = lastExitCode;
                            final Command fCommand = command;

                            startCallback();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        fCommand.onCommandResultListener.onCommandResult(fCommand.code, fExitCode, fBuffer);
                                    } finally {
                                        endCallback();
                                    }
                                }
                            });
                        } else {
                            command.onCommandResultListener.onCommandResult(command.code, lastExitCode, buffer);
                        }
                    }
                }

                command = null;
                buffer = null;
                idle = true;
                runNextCommand();
            }
        }

        /**
         * Process a normal STDOUT/STDERR line
         *
         * @param line     Line to process
         * @param listener Callback to call or null
         */
        private synchronized void processLine(String line, StreamGobbler.OutputCallback listener) {
            if (listener != null) {
                if (handler != null) {
                    final String fLine = line;
                    final StreamGobbler.OutputCallback fListener = listener;

                    startCallback();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                fListener.onOutput(fLine);
                            } finally {
                                endCallback();
                            }
                        }
                    });
                } else {
                    listener.onOutput(line);
                }
            }
        }

        /**
         * Add line to internal buffer
         *
         * @param line Line to add
         */
        private synchronized void addBuffer(String line) {
            if (buffer != null) {
                buffer.add(line);
            }
        }

        /**
         * Increase callback counter
         */
        private void startCallback() {
            synchronized (callbackSync) {
                callbacks++;
            }
        }

        /**
         * Decrease callback counter, signals callback complete state when dropped to 0
         */
        private void endCallback() {
            synchronized (callbackSync) {
                callbacks--;
                if (callbacks == 0) {
                    callbackSync.notifyAll();
                }
            }
        }

        /**
         * Internal call that launches the shell, starts gobbling, and stars executing commands.
         * See {@link Shell.Interactive}
         *
         * @return Opened successfully ?
         */
        private synchronized boolean open() {
            if (isDebug()) log(String.format("[%s%%] START", shell.toUpperCase()));

            try {
                // setup our process, retrieve STDIN stream, and STDOUT/STDERR gobblers
                if (environment.size() == 0) {
                    process = Runtime.getRuntime().exec(shell);
                } else {
                    Map<String, String> newEnvironment = new HashMap<String, String>();
                    newEnvironment.putAll(System.getenv());
                    newEnvironment.putAll(environment);
                    int i = 0;
                    String[] env = new String[newEnvironment.size()];
                    for (Map.Entry<String, String> entry : newEnvironment.entrySet()) {
                        env[i] = entry.getKey() + "=" + entry.getValue();
                        i++;
                    }
                    process = Runtime.getRuntime().exec(shell, env);
                }

                STDIN = new DataOutputStream(process.getOutputStream());
                STDOUT = new StreamGobbler(shell.toUpperCase() + "-", process.getInputStream(), new StreamGobbler.OutputCallback() {
                    @Override
                    public void onOutput(String line) {
                        if (line.startsWith(command.marker)) {
                            try {
                                lastExitCode = Integer.valueOf(line.substring(command.marker.length() + 1), 10);
                            } catch (Exception e) {
                            }
                            lastMarkerSTDOUT = command.marker;
                            processMarker();
                        } else {
                            addBuffer(line);
                            processLine(line, onSTDOUTLineListener);
                        }
                    }
                });
                STDERR = new StreamGobbler(shell.toUpperCase() + "*", process.getErrorStream(), new StreamGobbler.OutputCallback() {
                    @Override
                    public void onOutput(String line) {
                        if (line.startsWith(command.marker)) {
                            lastMarkerSTDERR = command.marker;
                            processMarker();
                        } else {
                            if (wantSTDERR) addBuffer(line);
                            processLine(line, onSTDERRLineListener);
                        }
                    }
                });

                // start gobbling and write our commands to the shell
                STDOUT.start();
                STDERR.start();

                running = true;
                closed = false;

                runNextCommand();

                return true;
            } catch (IOException e) {
                // shell probably not found
                return false;
            }
        }

        /**
         * Close shell and clean up all resources. Call this when you are done with the shell.
         * If the shell is not idle (all commands completed) you should not call this method
         * from the main UI thread because it may block for a long time. This method will
         * intentionally crash your app (if in debug mode) if you try to do this anyway.
         */
        public void close() {
            boolean _idle = isIdle(); // idle must be checked synchronized

            synchronized (this) {
                if (!running) return;
                running = false;
                closed = true;
            }

            // This method should not be called from the main thread unless the shell is idle
            // and can be cleaned up with (minimal) waiting. Only throw in debug mode.
            if (!_idle && isDebug() && (Looper.myLooper() != null) && (Looper.myLooper() == Looper.getMainLooper())) {
                log(ShellOnMainThreadException.EXCEPTION_NOT_IDLE);
                throw new ShellOnMainThreadException(ShellOnMainThreadException.EXCEPTION_NOT_IDLE);
            }

            if (!_idle) waitForIdle();

            try {
                STDIN.writeBytes("exit\n");
                STDIN.flush();

                // wait for our process to finish, while we gobble away in the background
                process.waitFor();

                // make sure our threads are done gobbling, our streams are closed, and the process is
                // destroyed - while the latter two shouldn't be needed in theory, and may even produce
                // warnings, in "normal" Java they are required for guaranteed cleanup of resources, so
                // lets be safe and do this on Android as well
                try {
                    STDIN.close();
                } catch (IOException e) {
                }
                STDOUT.join();
                STDERR.join();
                process.destroy();
            } catch (IOException e) {
                // shell probably not found
            } catch (InterruptedException e) {
                // this should really be re-thrown
            }

            if (isDebug()) log(String.format("[%s%%] END", shell.toUpperCase()));
        }

        /**
         * Is out shell still running ?
         *
         * @return Shell running ?
         */
        public boolean isRunning() {
            try {
                // if this throws, we're still running
                process.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
            }
            return true;
        }

        /**
         * Have all commands completed executing ?
         *
         * @return Shell idle ?
         */
        public synchronized boolean isIdle() {
            if (!isRunning()) {
                idle = true;
                synchronized (idleSync) {
                    idleSync.notifyAll();
                }
            }
            return idle;
        }

        /**
         * <p>Wait for idle state. As this is a blocking call, you should not call it from the main UI thread.
         * If you do so and debug mode is enabled, this method will intentionally crash your app.</p>
         * <p/>
         * <p>If not interrupted, this method will not return until all commands have finished executing.
         * Note that this does not necessarily mean that all the callbacks have fired yet.</p>
         * <p/>
         * <p>If no Handler is used, all callbacks will have been executed when this method returns. If
         * a Handler is used, and this method is called from a different thread than associated with the
         * Handler's Looper, all callbacks will have been executed when this method returns as well.
         * If however a Handler is used but this method is called from the same thread as associated
         * with the Handler's Looper, there is no way to know.</p>
         * <p/>
         * <p>In practise this means that in most simple cases all callbacks will have completed when this
         * method returns, but if you actually depend on this behavior, you should make certain this is
         * indeed the case.</p>
         * <p/>
         * <p>See {@link Shell.Interactive} for further details on threading and handlers</p>
         *
         * @return True if wait complete, false if wait interrupted
         */
        public boolean waitForIdle() {
            if (isDebug() && (Looper.myLooper() != null) && (Looper.myLooper() == Looper.getMainLooper())) {
                log(ShellOnMainThreadException.EXCEPTION_WAIT_IDLE);
                throw new ShellOnMainThreadException(ShellOnMainThreadException.EXCEPTION_WAIT_IDLE);
            }

            if (isRunning()) {
                synchronized (idleSync) {
                    while (!idle) {
                        try {
                            idleSync.wait();
                        } catch (InterruptedException e) {
                            return false;
                        }
                    }
                }

                if (
                        (handler != null) &&
                                (handler.getLooper() != null) &&
                                (handler.getLooper() != Looper.myLooper())
                        ) {
                    // If the callbacks are posted to a different thread than this one, we can wait until
                    // all callbacks have called before returning. If we don't use a Handler at all,
                    // the callbacks are already called before we getIcon here. If we do use a Handler but
                    // we use the same Looper, waiting here would actually block the callbacks from being
                    // called

                    synchronized (callbackSync) {
                        while (callbacks > 0) {
                            try {
                                callbackSync.wait();
                            } catch (InterruptedException e) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }

        /**
         * Are we using a Handler to post callbacks ?
         *
         * @return Handler used ?
         */
        public boolean hasHandler() {
            return (handler != null);
        }
    }


    /**
     * Thread utility class continuously reading from an InputStream
     */
    public static class StreamGobbler extends Thread {
        /**
         * Line callback interface
         */
        public interface OutputCallback {
            /**
             * <p>Line callback</p>
             * <p/>
             * <p>This callback should process the line as quickly as possible.
             * Delays in this callback may pause the native process or even
             * result in a deadlock</p>
             *
             * @param line String that was gobbled
             */
            public void onOutput(String line);
        }

        private String shell = null;
        private BufferedReader reader = null;
        private List<String> writer = null;
        private OutputCallback listener = null;

        /**
         * <p>StreamGobbler constructor</p>
         * <p/>
         * <p>We use this class because shell STDOUT and STDERR should be read as quickly as
         * possible to prevent a deadlock from occurring, or Process.waitFor() never
         * returning (as the buffer is full, pausing the native process)</p>
         *
         * @param shell       Name of the shell
         * @param inputStream InputStream to read from
         * @param outputList  List<String> to write to, or null
         */
        public StreamGobbler(String shell, InputStream inputStream, List<String> outputList) {
            this.shell = shell;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = outputList;
        }

        /**
         * <p>StreamGobbler constructor</p>
         * <p/>
         * <p>We use this class because shell STDOUT and STDERR should be read as quickly as
         * possible to prevent a deadlock from occurring, or Process.waitFor() never
         * returning (as the buffer is full, pausing the native process)</p>
         *
         * @param shell          Name of the shell
         * @param inputStream    InputStream to read from
         * @param outputCallback OnLineListener callback
         */
        public StreamGobbler(String shell, InputStream inputStream, OutputCallback outputCallback) {
            this.shell = shell;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            listener = outputCallback;
        }

        @Override
        public void run() {
            // keep reading the InputStream until it ends (or an error occurs)
            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (isDebug()) log(String.format("[%s] %s", shell, line));
                    if (writer != null) writer.add(line);
                    if (listener != null) listener.onOutput(line);
                }
            } catch (IOException e) {
            }

            // make sure our stream is closed and resources will be freed
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }


    public static class Helper {
        /**
         * Remount a path file as the type.
         *
         * @param path      the path you want to remount
         * @param mountType the mount type, including, <i>"ro" means read only, "rw" means read and write</i>
         * @return the operation result.
         */
        public boolean remount(String path, String mountType) {
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(mountType)) {
                return false;
            }

            if (mountType.equalsIgnoreCase("rw") || mountType.equalsIgnoreCase("ro")) {
                return Remounter.remount(path, mountType);
            } else {
                return false;
            }

        }
    }

    /**
     * Exception class used to notify developer that a shell was not close()d
     */
    public static class ShellNotClosedException extends RuntimeException {
        public static final String EXCEPTION_NOT_CLOSED = "Application did not close() interactive shell";

        public ShellNotClosedException() {
            super(EXCEPTION_NOT_CLOSED);
        }
    }


    /**
     * Exception class used to crash application when shell commands are executed
     * from the main thread, and we are in debug mode.
     */

    public static class ShellOnMainThreadException extends RuntimeException {
        public static final String EXCEPTION_COMMAND = "Application attempted to run a shell command from the main thread";
        public static final String EXCEPTION_NOT_IDLE = "Application attempted to wait for a non-idle shell to close on the main thread";
        public static final String EXCEPTION_WAIT_IDLE = "Application attempted to wait for a shell to become idle on the main thread";

        public ShellOnMainThreadException(String message) {
            super(message);
        }
    }

/**
 *
 * http://en.wikipedia.org/wiki/Chmod
 *
 *
 *
 * 0  =  ---  =  no access
 1  =  --x  =  execute
 2  =  -w-  =  write
 3  =  -wx  =  write and execute
 4  =  r--  =  read
 5  =  r-x  =  read and execute
 6  =  rw-  =  read and write
 7  =  rwx  =  read write execute (full access)


 #	Permission	rwx
 7	full	111
 6	read and write	110
 5	read and execute	101
 4	read only	100
 3	write and execute	011
 2	write only	010
 1	execute only	001
 0	none	000

 Symbolic Notation	Octal Notation	English
 ----------	0000	no permissions
 ---x--x--x	0111	execute
 --w--w--w-	0222	write
 --wx-wx-wx	0333	write & execute
 -r--r--r--	0444	read
 -r-xr-xr-x	0555	read & execute
 -rw-rw-rw-	0666	read & write
 -rwxrwxrwx	0777	read, write, & execute

 **/
}
