package com.mcxiaoke.apptoolkit.shell.command;

/**
 * Project: filemanager
 * Package: com.mcxiaoke.appmanager.shell.command
 * User: mcxiaoke
 * Date: 13-6-12
 * Time: 下午8:55
 */
public interface Command<Result> {

    public Result execute() throws Exception;

    public String getName();

    public int getUid();

    public String getCommand();

    public boolean isRoot();

    public boolean isAsync();
}
