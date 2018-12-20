//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.util.LinkedList;

public abstract class Appender implements Runnable {
    PropertyConfigure mConfigure;
    Thread mThread;
    int bufferSize;
    LinkedList<LogEvent> mBuffer;
    boolean alive;

    public Appender(PropertyConfigure configure, int buffer) {
        this.bufferSize = 0;
        this.mBuffer = new LinkedList();
        this.alive = true;
        this.mConfigure = configure;
        if (buffer > 0) {
            this.bufferSize = buffer;
            this.mThread = new Thread(this, "Appender");
            this.mThread.setPriority(3);
            this.mThread.start();
        }

    }

    public Appender(PropertyConfigure configure) {
        this(configure, 0);
    }

    public abstract void append(LogEvent var1);

    protected boolean enable(Level level) {
        return this.mConfigure.getLevel().isGreaterOrEqual(level);
    }

    public void reopen(PropertyConfigure configure) {
        if (this.mThread == null) {
            this.alive = true;
            this.mThread = new Thread(this, "Appender");
            this.mThread.setPriority(3);
            this.mThread.start();
        }

    }

    public void appendToQueue(LogEvent e) {
        if (this.mThread == null) {
            this.append(e);
        } else {
            LinkedList var2 = this.mBuffer;
            synchronized(this.mBuffer) {
                this.mBuffer.add(e);
                this.mBuffer.notify();
            }
        }

    }

    public void close() {
        if (this.mThread != null) {
            this.alive = false;
            LinkedList var1 = this.mBuffer;
            synchronized(this.mBuffer) {
                this.mBuffer.clear();
                this.mBuffer.notify();
            }

            this.mThread = null;
        }

    }

    public void run() {
        while(true) {
            try {
                if (this.alive) {
                    LogEvent ev = null;
                    LinkedList var2 = this.mBuffer;
                    synchronized(this.mBuffer) {
                        while(this.mBuffer.size() < 1) {
                            if (!this.alive) {
                                return;
                            }

                            this.mBuffer.wait();
                        }

                        ev = (LogEvent)this.mBuffer.removeFirst();
                    }

                    if (ev != null) {
                        this.append(ev);
                    }
                    continue;
                }
            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }

            return;
        }
    }
}
