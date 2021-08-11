package com.demo.usermng.exception;

public class RealmHandlingException extends RuntimeException {
    public RealmHandlingException(String arg0) {
        super(arg0);
    }

    public RealmHandlingException(Throwable arg0) {
        super(arg0);
    }

    public RealmHandlingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public RealmHandlingException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
