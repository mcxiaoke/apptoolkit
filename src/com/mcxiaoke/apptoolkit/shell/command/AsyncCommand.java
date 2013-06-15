package com.mcxiaoke.apptoolkit.shell.command;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.shell.command
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午9:02
 */
public abstract class AsyncCommand<Result> implements Command<Result> {

    @Override
    public boolean isAsync() {
        return true;
    }
}
