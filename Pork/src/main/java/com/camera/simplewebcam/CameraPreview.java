package com.camera.simplewebcam;

import android.graphics.Bitmap;


import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CameraPreview {

    private static final int IMG_WIDTH = 640;
    private static final int IMG_HEIGHT = 480;
    private static CameraPreview mCameraPreview = null;

    static {
        System.loadLibrary("ImageProc");
    }

    private int cameraId = -1;
    private int cameraBase = 0;

    //默认值
    private int year;
    private int month;
    private int dayOfMonth;

    private CameraPreview() throws Exception {

        int ret;

        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH) + 1;
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        do {
            ret = prepareCameraWithBase(++cameraId, cameraBase);
        } while (ret == -1 && cameraId <= 6);

        if (ret == -1)
            throw new Exception("no camera id exists:" + cameraId);
        else
            stopCamera();
    }

    public native int prepareCameraWithBase(int videoid, int camerabase);

    public native void stopCamera();

    public static CameraPreview getInstance() {
        if (mCameraPreview == null) {
            synchronized (CameraPreview.class) {
                try {
                    mCameraPreview = new CameraPreview();
                } catch (Exception e) {
                    LogUtil.e("拍照初始化异常！");
                }
            }
        }
        return mCameraPreview;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public native int prepareCamera(int videoid);

    public String taskPic(String imgPath) throws Exception {

        int ret = prepareCameraWithBase(cameraId, cameraBase);

        if (ret == -1) {
            LogUtil.e("taskPic cameraid no exists:" + cameraId + " ret:" + ret);
            throw new Exception("no camerid exists:" + cameraId);
        }

        processCamera();

        Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);

        pixeltobmp(bmp);

        String fileName = saveBitmap(bmp, imgPath);

        stopCamera();

        return fileName;
    }

    public native void processCamera();

    public native void pixeltobmp(Bitmap bitmap);

    private String saveBitmap(Bitmap bm, String fp) throws Exception {

        String imgName = this.year + "/" + this.month + "/" + this.dayOfMonth + "/" + fp;

        File f = new File(FileHelp.getImgDir() + imgName);

        File parentFile = f.getParentFile();

        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
            if (!mkdirs) {
                f = new File(FileHelp.getImgDir() + fp);
            }
        }

        if (f.exists()) {
            boolean delete = f.delete();
            if (!delete)
                LogUtil.e("删除本地文件失败：" + f.getAbsolutePath());
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 88, out);
            out.flush();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogUtil.e(e.getMessage());
                }
            }
        }
        return imgName;
    }
}
