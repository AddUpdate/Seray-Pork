package com.skyworth.splicing;

import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortUtil {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String serialPath;
    private int baudrate;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop;

    // 用于多线程读取操作
    private boolean enableRead = false;
    private Handler handler = new Handler();
    private Runnable timeoutRun = new Runnable() {
        @Override
        public void run() {
            if (isStop)
                return;
            SerialPortUtil.this.closeSerialPort();
            SerialPortUtil.this.callDataReceiveListener(DataReceType.timeout, null, -1);
        }
    };

    public SerialPortUtil(String serialName, int baudrate) throws SecurityException, IOException {
        this.serialPath = serialName;
        this.baudrate = baudrate;
        this.onCreate();
    }

    private void onCreate() throws SecurityException, IOException {
        mSerialPort = new SerialPort(new File(serialPath), baudrate);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        isStop = false;
        mReadThread.start();
    }

    public boolean sendBuffer(byte[] mBuffer) {
        this.pauseReadThread();
        boolean result = true;
        String tail = "";
        byte[] tailBuffer = tail.getBytes();
        byte[] mBufferTemp = new byte[mBuffer.length + tailBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);
        try {
            this.clearReadBuffer();
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    // 针对多线程读取，暂停读取数据操作
    private void pauseReadThread() {
        this.enableRead = false;
    }

    private void clearReadBuffer() throws IOException {
        if (mInputStream != null) {
            mInputStream.available();
        }
    }

    public void readBuffer(int timeout, OnDataReceiveListener receive) {

        this.onDataReceiveListener = receive;

        handler.postDelayed(timeoutRun, timeout);

        this.continueReadThread();
    }

    // 针对多线程读取，继续读取数据操作
    private void continueReadThread() {
        this.enableRead = true;
    }

    private int readBuffer() {
        if (mInputStream == null)
            return -1;
        int size = 0, count;
        byte[] buffer = new byte[1024];
        try {
            do {
                if (this.isStop)
                    return -1;
                Thread.sleep(15);
                if (this.isStop)
                    return -1;
                count = mInputStream.read(buffer, size, buffer.length - size);
                size += count;
                if (size == buffer.length) {
                    byte[] temp = new byte[size * 2];
                    System.arraycopy(buffer, 0, temp, 0, size);
                    buffer = temp;
                }
            } while (count > 0);
            if (size > 0)
                this.callDataReceiveListener(DataReceType.success, buffer, size);
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            this.callDataReceiveListener(DataReceType.faild, null, -1);
        }
        return -1;
    }

    private void callDataReceiveListener(DataReceType type, byte[] buffer, int size) {
        if (this.onDataReceiveListener == null)
            return;

        // 避免重复调用监听函数
        OnDataReceiveListener temp = this.onDataReceiveListener;
        this.onDataReceiveListener = null;
        if (type != DataReceType.timeout) { // 当前如果不是超时，则取消超时回调
            handler.removeCallbacks(timeoutRun);
        }
        temp.onDataReceive(type, buffer, size);
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if (isStop)
            return;
        isStop = true;
        this.pauseReadThread();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        if (mReadThread != null) {
            mReadThread = null;
        }
    }

    // 数据接收枚举
    public enum DataReceType {
        success, // 成功,可以正常读取数据
        faild, // 失败，不能读取数据
        timeout// 超时，不能读取数据
    }

    public interface OnDataReceiveListener {
        void onDataReceive(DataReceType type, byte[] buffer, int size);
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isStop) {
                if (SerialPortUtil.this.enableRead) {
                    SerialPortUtil.this.readBuffer();
                }
            }
        }
    }
}
