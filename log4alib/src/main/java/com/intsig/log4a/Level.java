//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

public class Level {
    public static final int OFF_INT = 1000;
    public static final int ERROR_INT = 2000;
    public static final int WARN_INT = 3000;
    public static final int INFO_INT = 5000;
    public static final int DEBUG_INT = 4000;
    public static final Level OFF = new Level(1000, "OFF");
    public static final Level ERROR = new Level(2000, "ERROR");
    public static final Level WARN = new Level(3000, "WARN");
    public static final Level INFO = new Level(5000, "INFO");
    public static final Level DEBUG = new Level(4000, "DEBUG");
    int level;
    String levelStr;

    private Level(int level, String levelStr) {
        this.level = level;
        this.levelStr = levelStr;
    }

    public boolean isGreaterOrEqual(Level l) {
        return this.level >= l.level;
    }
}
