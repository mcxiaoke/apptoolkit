package com.mcxiaoke.apptoolkit.shell;

import com.mcxiaoke.apptoolkit.shell.command.Command;
import com.mcxiaoke.apptoolkit.shell.command.CommandCallback;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.console.shell
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午9:09
 */
public interface Shell {

    public void execute(Command<?> cmd, CommandCallback<?, ?> callback);
}
