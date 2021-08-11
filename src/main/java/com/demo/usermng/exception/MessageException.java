package com.demo.usermng.exception;

public class MessageException extends Exception {
    public MessageException(String arg0) {
        super(arg0);
    }

    public MessageException(Throwable arg0) {
        super(arg0);
    }

    public MessageException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public MessageException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }
}
