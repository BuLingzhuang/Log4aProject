//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.intsig.log4a;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

public abstract class Appender implements Runnable {
    PropertyConfigure mConfigure;
    Thread mThread;
    int bufferSize;
    LinkedList<LogEvent> mBuffer;
    boolean alive;
    private WriteListener writeListener;
    static final String FILE_NAME_HEAD = "log-";
    static final String CACHE_FILE_NAME_HEAD = "cacheLog-";
    static final String CACHE_FILE_NAME = "CamScanner";
    static final String FILE_NAME_FOOT = ".log";

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

    public File[] getHistoryLogFiles() {
        File[] result = null;
        String dir = mConfigure.getLogDir();

        File logDir = new File(dir);
        if (logDir.exists()) {
            result = logDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(FILE_NAME_HEAD) && filename.endsWith(FILE_NAME_FOOT);
                }
            });
        }
        return result;
    }

    public void setWriteListener(WriteListener writeListener) {
        this.writeListener = writeListener;
    }

    @Override
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
                            if (writeListener != null) {
                                writeListener.finish();
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

    public interface WriteListener {
        void finish();
    }
}
