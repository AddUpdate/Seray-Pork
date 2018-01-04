package com.lzscale.scalelib.misclib;

import com.seray.utils.LogUtil;
import com.seray.utils.NumFormatUtil;

public class SerialPort {

    static {
        System.loadLibrary("Misc");
    }

    private int mFd = -1;

    private String path = "null";

    public SerialPort(final String str) {
        try {
            mFd = openPort(str);
            path = str;
        } catch (Exception e) {
            LogUtil.e("SerialPort---openPort()异常..." + e.getMessage());
        }
        if (mFd < 0) {
            LogUtil.e("SerialPort: open faile");
        }
    }

    private native int openPort(final String str);

    @Override
    protected void finalize() {
        String content = "CreateAt:" + NumFormatUtil.getLogCreateAt() + "," + "PortId:" + mFd +
                "," + "PortPath:" + path
                + ";" + "\r\n";
        try {
           // FileHelp.writeToLog(content);
            close(mFd);
        } catch (Exception e) {// 捕获不到异常消息
            LogUtil.e("SerialPort---close()异常..." + e.getMessage());
        }
    }

    private native void close(int fd);

    public void setParameter(int baud, int dataBit, int stopBit, int check) {
        if (mFd > 0)
            try {
                setParameter(mFd, baud, dataBit, stopBit, check);
            } catch (Exception e) {
                LogUtil.e("SerialPort---setParameter()异常..." + e.getMessage());
            }
    }

    private native void setParameter(int fd, int baud, int dataBit, int stopBit, int check);

    public int send(final String str) {
        try {
            if (mFd > 0)
                return send(mFd, str);
            else
                return -1;
        } catch (Exception e) {
            LogUtil.e("SerialPort---send()异常..." + e.getMessage());
        }
        return -1;
    }

    private native int send(int fd, final String str);

    public int send(final byte[] s) {
        try {
            if (mFd > 0)
                return sendBytes(mFd, s);
            else
                return -1;
        } catch (Exception e) {
            LogUtil.e("SerialPort---sendBytes()异常..." + e.getMessage());
        }
        return -1;
    }

    private native int sendBytes(int fd, final byte[] s);

    public byte[] read(int n, int ms) {
        try {
            if (mFd > 0)
                return read(mFd, n, ms);
            else
                return null;
        } catch (Exception e) {
            LogUtil.e("SerialPort---read()异常..." + e.getMessage());
        }
        return null;
    }

    private native byte[] read(int fd, int len, int ms);

    public byte[] readAll() {
        try {
            if (mFd > 0)
                return readAll(mFd);
            else
                return null;
        } catch (Exception e) {
            LogUtil.e("SerialPort---readAll()异常..." + e.getMessage());
        }
        return null;
    }

    private native byte[] readAll(int fd);

}
