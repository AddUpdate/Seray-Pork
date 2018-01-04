package com.lzscale.scalelib.misclib;

public class BackDisplay {

    static {
        System.loadLibrary("Misc");
    }

    private SerialPort mPort = new SerialPort("/dev/ttymxc4");// 4

    public BackDisplay() {
        mPort.setParameter(9600, 8, 1, 'n');
    }

    public void DispLine1(final String str) {
        String str1 = "d1" + str + "\r\n";
        mPort.send(str1);
    }

    public void DispLine2(final String str) {
        String str1 = "d2" + str + "\r\n";
        mPort.send(str1);
    }

    public void DispLine3(String str) {
        String str1 = "d3" + str + "\r\n";
        mPort.send(str1);
    }

    public void DispLine4(String str) {
        String str1 = "d4" + str + "\r\n";
        mPort.send(str1);
    }

    public void DispLed1(boolean flag) {
        if (flag)
            mPort.send("l11\r\n");
        else
            mPort.send("l10\r\n");
    }

    public void DispLed2(boolean flag) {
        if (flag)
            mPort.send("l21\r\n");
        else
            mPort.send("l20\r\n");
    }
}
