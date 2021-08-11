package com.demo.usermng.exception;

public class UserHandlingException extends RuntimeException {
    public UserHandlingException(String arg0) {
        super(arg0);
    }

    public UserHandlingException(Throwable arg0) {
        super(arg0);
    }

    public UserHandlingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public UserHandlingException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
