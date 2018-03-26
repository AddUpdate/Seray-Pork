package com.seray.card;

public abstract class PayBase {

    // 调试TAG
    public static String TAG = "PayTag";

    // 是否已锁定再次发起请求，保证必须先进先出
    private static boolean _isLocked = false;
    protected ReturnValueCallback returnValueCallback;

    protected int timeout;

    protected PayBase(int timeout) {
        this.timeout = timeout;
    }

    public static int byte2Int(byte[] b) {
        return byte2Int(b, 0, b.length);
    }

    public static int byte2Int(byte[] b, int offset, int len) {
        int intValue = 0, end = offset + len;
        for (int i = offset; i < end; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }

    // 读卡
    public abstract void readCardNumber(ReturnValueCallback callback);

    // 取消读卡
    public abstract void cancelReadCard(ReturnValueCallback callback);

    // 是否可以发起请求，并会锁定
    protected boolean isCanLaunchRequest(ReturnValueCallback callback) {
        if (this.getIsLock()) {
            if (callback != null) {
                callback.run(new ReturnValue(false, "99", "请等待上次通讯请求完成"));
            }
            return false;
        }
        this.setLock();
        this.returnValueCallback = callback;
        return true;
    }

    // 获取是否可以发起请求，避免上一次请求没有完成
    public boolean getIsLock() {
        return _isLocked;
    }

    // 锁定再次发起请求
    public void setLock() {
        _isLocked = true;
    }

    protected void callReturnValueCallback(ReturnValue result) {
        _isLocked = false;
        if (returnValueCallback != null) {
            returnValueCallback.run(result);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        _isLocked = false;
        super.finalize();
    }

    public String format(String f, Object o) {
        return String.format(f, o);
    }

    // 主要用于输出显示
    public String getHexStr(byte[] buffer, int size) {
        return this.getHexStr(buffer, 0, size, " ");
    }

    public String getHexStr(byte[] buffer, int offset, int len, String split) {
        StringBuilder str = new StringBuilder();
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str.append(hex).append(split);
        }
        return str.toString().toUpperCase();
    }

    public String getHexStr(byte[] buffer, int offset, int len) {
        return this.getHexStr(buffer, offset, len, "");
    }

    public void copyToByte(String str, byte[] data, int offset) {
        for (int i = 0; i < str.length(); i++) {
            data[i + offset] = (byte) str.charAt(i);
        }
    }

    public byte getXORBack(byte[] data, int index, int end) {
        byte res = this.getXOR(data, index, end);
        return (byte) (~((int) res));
    }

    public byte getXOR(byte[] data, int index, int end) {
        byte t = 0;
        for (int i = index; i < end; i++)
            t ^= data[i];
        return t;
    }
}
