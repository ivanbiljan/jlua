package com.jlua.exceptions;

public final class LuaException extends Exception {
    public LuaException(String message) {
        super(message);
    }

    public LuaException(Exception ex) {
        super(ex);
    }

    public LuaException(String message, Exception ex) {
        super(message, ex);
    }
}
