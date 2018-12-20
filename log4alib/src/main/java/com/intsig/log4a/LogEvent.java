//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LogEvent {
    String name;
    String message;
    Throwable throwable;
    Level level;

    public LogEvent(String name, Level level, String message, Throwable throwable) {
        this.name = name;
        this.message = message;
        this.throwable = throwable;
        this.level = level;
    }

    public String dump(PropertyConfigure mConfigure) {
        return mConfigure.layout(this);
    }

    protected String stackTrace() {
        if (this.throwable == null) {
            return "";
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            this.throwable.printStackTrace(ps);
            return baos.toString();
        }
    }
}
