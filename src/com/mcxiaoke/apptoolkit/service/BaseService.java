package com.mcxiaoke.apptoolkit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.service
 * User: mcxiaoke
 * Date: 13-6-15
 * Time: 下午5:56
 */
abstract class BaseService extends Service implements Handler.Callback {

    private static final String TAG = BaseService.class.getSimpleName();
    private static final String CLASS_NAME = BaseService.class.getName();

    protected static final void debug(String tag, String... messages) {
        if (messages != null && messages.length > 0) {
            for (String message : messages) {
                Log.v(tag, message);
            }
        }
    }

    public static final String EXTRA_CMD = CLASS_NAME + ".EXTRA_CMD";
    public static final String EXTRA_ID = CLASS_NAME + ".EXTRA_ID";
    public static final String EXTRA_KEY = CLASS_NAME + ".EXTRA_KEY";
    public static final String EXTRA_STATUS = CLASS_NAME + ".EXTRA_STATUS";
    public static final String EXTRA_MESSENGER = CLASS_NAME + ".EXTRA_MESSENGER";
    public static final String EXTRA_RESULT_RECEIVER = CLASS_NAME + ".EXTRA_RESULT_RECEIVER";
    public static final String EXTRA_STRING_LIST = CLASS_NAME + ".EXTRA_STRING_LIST";


    /**
     * command for do nothing
     */
    public static final int CMD_NONE = 0;
    /**
     * check task status ,running or not
     * need extra: id , type long
     */
    public static final int CMD_STATUS = -1001;
    /**
     * cancel task
     * need extra: id, type long
     */
    public static final int CMD_CANCEL = -1002;

    private final Object mLock = new Object();

    private boolean mDebug;

    private BaseService mBaseService;
    private NotificationManager mNotificationManager;
    private ExecutorService mExecutor;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUiHandler;

    private Map<Long, Runnable> mTasks;
    private Map<Long, Future<?>> mFutures;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isDebug()) {
            debug(TAG, "onCreate()");
        }
        mBaseService = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mTasks = Collections.synchronizedMap(new WeakHashMap<Long, Runnable>());
        mFutures = Collections.synchronizedMap(new WeakHashMap<Long, Future<?>>());
        ensureHandler();
        ensureExecutor();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        if (isDebug()) {
            debug(TAG, "onStartCommand()");
        }
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isDebug()) {
            debug(TAG, "onDestroy()");
        }
        destroyHandler();
        destroyExecutor();
        mTasks.clear();
        mFutures.clear();
    }

    private void handleIntent(final Intent intent) {
        if (intent == null) {
            if (isDebug()) {
                debug(TAG, "handleIntent() intent is null.");
            }
            return;
        }
        int cmd = intent.getIntExtra(EXTRA_CMD, 0);
        if (isDebug()) {
            debug(TAG, "handleIntent() cmd=" + cmd + " intent=" + intent.toString());
        }
        switch (cmd) {
            case CMD_STATUS:
                onCmdStatus(intent);
                break;
            case CMD_CANCEL:
                onCmdCancel(intent);
                break;
            case CMD_NONE:
                break;
            default:
                break;
        }

        final long taskId = System.currentTimeMillis();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isDebug()) {
                    debug(TAG, "handleIntent() run() START");
                }
                onHandleIntent(taskId, intent);
                if (isDebug()) {
                    debug(TAG, "handleIntent() run() END");
                }
                remove(taskId);
            }
        };

        Future<?> future = submit(runnable);
        mTasks.put(taskId, runnable);
        mFutures.put(taskId, future);
    }

    private void onCmdStatus(Intent intent) {
        long taskId = intent.getLongExtra(EXTRA_ID, 0);
        if (isDebug()) {
            debug(TAG, "onCmdStatus()taskId=" + taskId);
        }
        if (taskId > 0) {
            Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
            if (messenger != null) {
                boolean isRunning = isRunning(taskId);
                Message message = Message.obtain();
                message.what = CMD_STATUS;
                message.arg1 = isRunning ? 1 : 0;
                message.obj = isRunning;
            }
        }
    }

    private void onCmdCancel(Intent intent) {
        long taskId = intent.getLongExtra(EXTRA_ID, 0);
        if (isDebug()) {
            debug(TAG, "onCmdCancel()taskId=" + taskId);
        }
        if (taskId > 0) {
            cancel(taskId);
//            Messenger messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
//            if (messenger != null) {
//                boolean isRunning = isRunning(taskId);
//                Message message = Message.obtain();
//                message.what = CMD_CANCEL;
//                message.arg1 = isRunning ? 1 : 0;
//                message.obj = isRunning;
//            }
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        if (isDebug()) {
            debug(TAG, "handleMessage() msg=" + msg);
        }
        return true;
    }

    private void remove(long taskId) {
        if (isDebug()) {
            debug(TAG, "remove() taskId=" + taskId);
        }
        mTasks.remove(taskId);
        mFutures.remove(taskId);
    }

    protected final void cancel(long taskId) {
        if (isDebug()) {
            debug(TAG, "cancel() taskId=" + taskId);
        }
        Runnable runnable = mTasks.remove(taskId);
        if (runnable != null) {
            if (runnable instanceof ExtendedRunnable) {
                ((ExtendedRunnable) runnable).cancel();
            }
        }
        Future<?> future = mFutures.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
    }

    protected final void cancelAllTasks() {
        Collection<Long> taskIds = mTasks.keySet();
        for (long id : taskIds) {
            cancel(id);
        }
        mTasks.clear();
        mFutures.clear();
    }

    protected final boolean isRunning(long taskId) {
        return mFutures.get(taskId) != null && mTasks.get(taskId) != null;
    }

    protected final void execute(Runnable runnable) {
        String name = "Runnable";
        ensureExecutor();
        if (runnable instanceof ExtendedRunnable) {
            name = ((ExtendedRunnable) runnable).getName();
        }
        if (isDebug()) {
            debug(TAG, "execute() name=" + name);
        }
        mExecutor.execute(runnable);
    }

    protected final Future<?> submit(Runnable runnable) {
        ensureExecutor();
        String name = "Runnable";
        if (runnable instanceof ExtendedRunnable) {
            name = ((ExtendedRunnable) runnable).getName();
        }
        if (isDebug()) {
            debug(TAG, "submit() name=" + name);
        }
        return mExecutor.submit(runnable);
    }

    protected final <T> Future<T> submit(Runnable runnable, T result) {
        ensureExecutor();
        return mExecutor.submit(runnable, result);
    }

    protected final <T> Future<T> submit(Callable<T> callable) {
        ensureExecutor();
        return mExecutor.submit(callable);
    }

    protected final ExecutorService getExecutor() {
        return ensureExecutor();
    }

    protected final BaseService getBaseService() {
        return this;
    }

    private void ensureHandler() {
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread(TAG);
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        if (mUiHandler == null) {
            mUiHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                }
            };
        }
    }

    private void destroyHandler() {
        synchronized (mLock) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if (mHandlerThread != null) {
                mHandlerThread.quit();
                mHandlerThread = null;
            }
            if (mUiHandler != null) {
                mUiHandler.removeCallbacksAndMessages(null);
                mUiHandler = null;
            }
        }
    }

    private ExecutorService ensureExecutor() {
        if (mExecutor == null) {
            mExecutor = supplyExecutor();
        }
        return mExecutor;
    }

    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
    }

    protected final void sendMessage(Message message) {
        if (mHandler != null) {
            mHandler.sendMessage(message);
        }
    }

    protected final void sendUiMessage(Message message) {
        if (mUiHandler != null) {
            mUiHandler.sendMessage(message);
        }
    }

    protected void runOnUiThread(Runnable runnable) {
        if (mUiHandler != null) {
            mUiHandler.post(runnable);
        }
    }

    protected final void showNotification(int id, Notification notification) {
        if (notification != null) {
            mNotificationManager.notify(id, notification);
        }
    }

    protected final void cancelNotification(int id) {
        mNotificationManager.cancel(id);
    }

    protected final void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    /**
     * can be override to use custom executor
     *
     * @return
     */
    protected ExecutorService supplyExecutor() {
        return Executors.newCachedThreadPool();
    }

    protected abstract void onHandleIntent(long taskId, Intent intent);

    protected abstract boolean isDebug();
}
