//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LogcatAppender extends Appender {
    Method error;
    Method debug;
    Method info;
    Method warn;
    boolean loaded = false;

    public LogcatAppender(PropertyConfigure configure) {
        super(configure);

        try {
            Class log = Class.forName("android.util.Log");
            this.error = log.getDeclaredMethod("e", String.class, String.class, Throwable.class);
            this.warn = log.getDeclaredMethod("w", String.class, String.class, Throwable.class);
            this.info = log.getDeclaredMethod("i", String.class, String.class, Throwable.class);
            this.debug = log.getDeclaredMethod("d", String.class, String.class, Throwable.class);
            this.loaded = true;
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
        } catch (SecurityException var4) {
            var4.printStackTrace();
        } catch (NoSuchMethodException var5) {
            var5.printStackTrace();
        }

    }

    public synchronized void append(LogEvent e) {
        if (this.loaded) {
            Level level = e.level;
            if (this.enable(level)) {
                try {
                    if (level.equals(Level.ERROR)) {
                        this.error.invoke((Object)null, e.name, e.message, e.throwable);
                    } else if (level.equals(Level.WARN)) {
                        this.warn.invoke((Object)null, e.name, e.message, e.throwable);
                    } else if (level.equals(Level.INFO)) {
                        this.info.invoke((Object)null, e.name, e.message, e.throwable);
                    } else if (level.equals(Level.DEBUG)) {
                        this.debug.invoke((Object)null, e.name, e.message, e.throwable);
                    }
                } catch (IllegalArgumentException var4) {
                    var4.printStackTrace();
                } catch (IllegalAccessException var5) {
                    var5.printStackTrace();
                } catch (InvocationTargetException var6) {
                    var6.printStackTrace();
                }
            }

        }
    }

    public void close() {
    }
}
