//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class Log4A {
    static final String VERSION = "0.1";
    static Appender mAppender;
    static boolean isInit = false;
    static String dir = null;
    static PropertyConfigure configure;
    static EncFileAppender sExtraAppender;
    static ArrayList<Logger> loggers = new ArrayList();

    public Log4A() {
    }

    public static void init() {
        init("/sdcard/log4a.properties");
    }

    public static void init(Properties properties) {
        init(properties, false);
    }

    /**
     * @param properties   参数配置
     * @param useLocalProp 是否加载本地配置
     */
    public static void init(Properties properties, boolean useLocalProp) {
        if (useLocalProp) {
            configure = new PropertyConfigure(properties, "/sdcard/log4a.properties");
        } else {
            configure = new PropertyConfigure(properties);
        }
        mAppender = configure.getAppender();
        dir = configure.getLogDir();
        if (mAppender instanceof LogcatAppender) {
            sExtraAppender = new EncFileAppender(configure, 20);
        }
    }

    public static void init(String properties) {
        if (!isInit) {
            configure = new PropertyConfigure(properties);
            mAppender = configure.getAppender();
            dir = configure.getLogDir();
            isInit = true;
            if (mAppender instanceof LogcatAppender) {
                sExtraAppender = new EncFileAppender(configure, 20);
            }

        }
    }

    public static String getLogFile() {
        if (mAppender instanceof FileAppender) {
            return ((FileAppender) mAppender).getCurrentLogFile();
        } else {
            return sExtraAppender != null ? sExtraAppender.getCurrentLogFile() : null;
        }
    }

    public static void clear() {
    }

    public static String getLodDir() {
        return dir;
    }

    public static void reopen() {
        if (configure != null) {
            mAppender.reopen(configure);
            dir = configure.getLogDir();
            isInit = true;
            if (sExtraAppender != null) {
                sExtraAppender.reopen(configure);
            }
        } else {
            init();
        }

    }

    public static void close() {
        Iterator var1 = loggers.iterator();

        while (var1.hasNext()) {
            Logger l = (Logger) var1.next();
            l.close();
        }

        loggers.clear();
        mAppender.close();
        isInit = false;
        if (sExtraAppender != null) {
            sExtraAppender.close();
        }

    }

    public static Logger getLogger(String name) {
        return new Logger(mAppender, name);
    }

    public static Logger getExtraLogger(String name) {
        return sExtraAppender != null ? new Logger(sExtraAppender, name) : null;
    }

    public static String geExtratLogFile() {
        return sExtraAppender != null ? sExtraAppender.getCurrentLogFile() : null;
    }
}
