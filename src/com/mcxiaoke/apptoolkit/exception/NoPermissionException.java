package com.mcxiaoke.apptoolkit.exception;

/**
 * Project: apptoolkit
 * Package: com.mcxiaoke.apptoolkit.exception
 * User: mcxiaoke
 * Date: 13-6-22
 * Time: 下午7:43
 */
public class NoPermissionException extends Exception {

    public NoPermissionException() {
    }

    public NoPermissionException(String detailMessage) {
        super(detailMessage);
    }

    public NoPermissionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoPermissionException(Throwable throwable) {
        super(throwable);
    }
}
