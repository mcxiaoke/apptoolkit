package com.mcxiaoke.appmanager.shell.command;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.shell.command
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午8:55
 */
public interface CommandCallback<Result, Progress> {

    public void onCommandSuccess(Result result, Command<Result> cmd);

    public void onCommandUpdate(Progress updateResult, Command<Result> cmd);

    public void onCommandFailed(Throwable throwable, Command<Result> cmd);
}
