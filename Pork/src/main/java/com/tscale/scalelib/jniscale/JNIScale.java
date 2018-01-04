package com.tscale.scalelib.jniscale;

public class JNIScale {

    static private JNIScale mScale = null;

    private float[] fdnTables = {1f, 2f, 5f, 10f, 20f, 50f, 100f, 200f, 500f};

    static {
        System.loadLibrary("JniScale");
    }

    private JNIScale() {
        createScale();
    }

    private native void createScale();

    static public JNIScale getScale() {
        if (mScale == null) {
            mScale = new JNIScale();
        }
        return mScale;
    }

    public float getDivisionValue() {
        int fdnPtr = getScale().getFdnPtr();
        return fdnTables[fdnPtr] / 1000;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        deleteScale();
    }

    public native void deleteScale();

    public native String getStringNet();

    public native String getStringTare();

    public native String getStringGross();

    public native float getFloatNet();

    public native float getFloatTare();

    public native float getFloatGross();

    public native boolean getStabFlag();

    public native boolean getTareFlag();

    public native boolean getZeroFlag();

    public native boolean tare();

    public native boolean zero();

    public native boolean pretare(float tare);

    public native int getInternelCount();

    public native int getUnit();

    public native String getStringUnit();

    public native boolean setUnit(int unit);

    public native boolean setStringUnit(String unit);

    public native boolean setStabDelay(int i); // 设置稳定延时，
    // 置（1-2000）2000稳定最慢，默认值150，大概是0.75秒

    /******************
     * 以下为设置和 标定需要用到的函数
     *********************/
    public native int getFdnPtr();

    public native int getBigFdnPtr();

    public native int getMainUnitDeci();

    public native int getMainUnit();

    public native float getMainUnitFull();

    public native float getMainUnitMidFull();

    public native boolean setFdnPtr(int ptr);

    public native boolean setBigFdnPtr(int ptr);

    public native boolean setMainUnitDeci(int deci);

    public native boolean setMainUnit(int unit);

    public native boolean setMainUnitFull(float full);

    public native boolean setMainUnitMidFull(float midfull);

    public native boolean saveNormalCalibration(int load0, int load1, float adw);

    public native boolean saveLinearCalibration(int calPoints, int load[], float calFull);

    public native int getAutoZeroRangePtr();

    public native boolean setAutoZeroRangePtr(int i);

    public native boolean setManualZeroRangePtr(int i);

    public native int getManualZeroRangePtr();

    public native boolean setZeroTrackPtr(int i);

    public native int getZeroTrackPtr();

    public native boolean setRangeMode(int i);

    public native int getRangeMode();

}
