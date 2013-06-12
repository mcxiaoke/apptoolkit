package com.mcxiaoke.appmanager.shell;

import com.mcxiaoke.appmanager.shell.command.Command;
import com.mcxiaoke.appmanager.shell.command.CommandCallback;

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
