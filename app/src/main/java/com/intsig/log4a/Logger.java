//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

public class Logger {
    String name;
    Appender mAppender;

    public Logger(Appender appender, String name) {
        this.name = name;
        this.mAppender = appender;
    }

    public Logger(Appender appender) {
        this.mAppender = appender;
        Thread.currentThread().getName();
    }

    public void debug(String message) {
        this.debug(this.name, message, (Throwable)null);
    }

    public void debug(String message, Throwable t) {
        this.debug(this.name, message, t);
    }

    public void debug(String tag, String message) {
        this.debug(tag, message, (Throwable)null);
    }

    public void debug(String tag, String message, Throwable t) {
        this.forcedLog(Level.DEBUG, tag, message, t);
    }

    public void info(String message) {
        this.info(this.name, message, (Throwable)null);
    }

    public void info(String message, Throwable t) {
        this.info(this.name, message, t);
    }

    public void info(String tag, String message) {
        this.info(tag, message, (Throwable)null);
    }

    public void info(String tag, String message, Throwable t) {
        this.forcedLog(Level.INFO, tag, message, t);
    }

    public void warn(String message) {
        this.warn(this.name, message, (Throwable)null);
    }

    public void warn(String message, Throwable t) {
        this.warn(this.name, message, t);
    }

    public void warn(String tag, String message) {
        this.warn(tag, message, (Throwable)null);
    }

    public void warn(String tag, String message, Throwable t) {
        this.forcedLog(Level.WARN, tag, message, t);
    }

    public void error(String message) {
        this.error(this.name, message, (Throwable)null);
    }

    public void error(String message, Throwable t) {
        this.error(this.name, message, t);
    }

    public void error(String tag, String message) {
        this.error(tag, message, (Throwable)null);
    }

    public void error(String tag, String message, Throwable t) {
        this.forcedLog(Level.ERROR, tag, message, t);
    }

    protected void forcedLog(Level level, String name, String message, Throwable t) {
        if (this.mAppender != null) {
            this.mAppender.appendToQueue(new LogEvent(name, level, message, t));
        }

    }

    public void close() {
    }
}
