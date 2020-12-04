//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

public class ConsoleAppender extends Appender {
    PropertyConfigure mConfigure;

    public ConsoleAppender(PropertyConfigure configure) {
        super(configure);
        this.mConfigure = configure;
        System.out.println("==========Begin of Log=========");
    }

    @Override
    public synchronized void append(LogEvent e) {
        if (this.enable(e.level)) {
            System.out.println(e.dump(this.mConfigure));
        }

    }

    @Override
    public void close() {
        System.out.println("==========End of Log=========");
    }
}
