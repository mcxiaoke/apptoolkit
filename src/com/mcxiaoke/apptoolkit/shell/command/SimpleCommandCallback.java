package com.mcxiaoke.apptoolkit.shell.command;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.shell.command
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午9:06
 */
public abstract class SimpleCommandCallback<Result> implements CommandCallback<Result, Void> {

    @Override
    public void onCommandFailed(Throwable throwable, Command<Result> cmd) {
    }

    @Override
    public void onCommandSuccess(Result result, Command<Result> cmd) {
    }

    @Override
    public void onCommandUpdate(Void updateResult, Command<Result> cmd) {
    }
}
