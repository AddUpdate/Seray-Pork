package com.lzscale.scalelib.misclib;

import com.seray.utils.LogUtil;

public class Printer {

    private static SerialPort mPort = new SerialPort("/dev/ttymxc1");

    static {
        System.loadLibrary("Misc");
    }

    public Printer() {
        mPort.setParameter(9600, 8, 1, 'n');
    }

    public void printASCIIString(final String str) {
        mPort.send(str);
    }

    public void PrintGBKString(final String str) {
        try {
            byte[] bs = str.getBytes("GBK");
            mPort.send(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void expand(int ex, int ey) // 字体放大，ex横向放大倍数， ey 纵向放大倍数，ex,ey
    // 范围是1-2，目前只支持放大到2倍
    {
        byte[] b = {0x1d, 0x21, 0x00};
        if (ex > 1 && ex <= 8)
            b[2] |= ((ex - 1) << 4);
        if (ey > 1 && ey <= 8)
            b[2] |= (ex - 1);
        mPort.send(b);
    }

    public void inverse(boolean b) // 反白打印
    {
        byte[] ba = {0x1d, 0x42, 0x00};
        if (b)
            ba[2] = 1;
        mPort.send(ba);
    }

    public void printQRCode(final String content, int size, CorrectionLevel level) // content
    // 为条码内容，size为每个小方块所占的点数(1-8)，level为纠错等级。
    {
        printQRCode(content.getBytes(), size, level);
    }

    public void printQRCode(byte[] content, int size, CorrectionLevel level) // content
    // 为条码内容，size为每个小方块所占的点数(1-8)，level为纠错等级。
    {

        byte[] ba = {0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43, 0x00};
        // ba[ba.length-1] = (byte)(0x30+size); -> ba[ba.length-1] = (byte)size;
        ba[ba.length - 1] = (byte) (size);
        mPort.send(ba);

        ba[ba.length - 2] = 0x45;
        switch (level) {
            case CORRECTION_L:
                ba[ba.length - 1] = '0';
                break;
            case CORRECTION_M:
                ba[ba.length - 1] = '1';
                break;
            case CORRECTION_Q:
                ba[ba.length - 1] = '2';
                break;
            case CORRECTION_H:
                ba[ba.length - 1] = '3';
                break;
        }
        mPort.send(ba);
        ba[3] = (byte) ((content.length + 3) & 0xff);
        ba[4] = (byte) ((content.length + 3) >> 8);
        ba[ba.length - 2] = 0x50;
        ba[ba.length - 1] = '0';
        mPort.send(ba);
        mPort.send(content);
        ba[3] = 0x03;
        ba[4] = 0x00;
        ba[ba.length - 2] = 0x51;
        mPort.send(ba);
    }

    @Override
    protected void finalize() {
        if (mPort != null) {
            try {
                mPort.finalize();
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

    enum CorrectionLevel// 纠错等级
    {
        CORRECTION_L, CORRECTION_M, CORRECTION_Q, CORRECTION_H
    }
}
