package com.seray.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import com.seray.entity.LocalFileTag;
import com.seray.entity.Products;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * 文本读写辅助类
 */
public class FileHelp {

    /**
     * 本应用的外部数据库
     */
    public static final String DATABASE_DIR = Environment.getExternalStorageDirectory() +
            "/seray/database/";
    /**
     * 用于储存静默拍照（库进出）的图片
     */
    public static final String DATA_PIC_DIR = Environment.getExternalStorageDirectory() +
            "/seray/picture/";
    /**
     * 用于储存最新APK及update.txt
     */
    public static final String UPDATE_APK_DIR = Environment.getExternalStorageDirectory() +
            "/seray/update/";
    /**
     * 进销存图片
     */
    public static final String STOCK_DIR = Environment.getExternalStorageDirectory() +
            "/seray/stock/";
    /**
     * 本地设备基本信息（设备号）
     */
    private static final String DEVICE_PATH = "/data/seray/config/device.cfg";
    /**
     * 本地绑定的POS机信息
     */
    private static final String POS_DEVICE_PATH = "/data/seray/config/pos_device.cfg";
    /**
     * 本地屏幕校准文件
     */
    private static final String CALIBRATION_PATH = "/data/system/calibration";
    /**
     * 用于记录用户选择展示商品
     */
    private static final String SEL_PRODUCT_PATH = Environment.getExternalStorageDirectory()
            + "/seray/selected/products.txt";
    /**
     * 用于记录用户搜索历史(最近三条)
     */
    private static final String SEARCH_PRODUCT_PATH = Environment.getExternalStorageDirectory()
            + "/seray/search/search.txt";
    /**
     * 用于记录版本升级下载是否完整(true false)
     */
    private static final String UPDATE_CHECK_PATH = Environment.getExternalStorageDirectory()
            + "/seray/update/update.txt";
    /**
     * 用于记录打印机和地秤后显示串口释放
     */
    private static final String LOG_PATH = Environment.getExternalStorageDirectory() +
            "/seray/logs/Log.txt";
    /**
     * 用于写入上位机下发的常用客户
     */
    private static final String CUSTOMER_PATH = Environment.getExternalStorageDirectory()
            + "/seray/customer/customer.txt";
    /**
     * 用于重印上笔交易
     */
    private static final String REPRINT_PATH = Environment.getExternalStorageDirectory() +
            "/seray/reprint/reprint.txt";

    /**
     * 厂内库 进出静默拍照 图片的转码
     */
    public static String encodeLibraryImg(String imgName) {
        if (imgName == null || imgName.isEmpty() || imgName.equals("null"))
            return "";
        return encodeImage(DATA_PIC_DIR, imgName);
    }
    /**
     * 进货图片的转码
     */
    public static String encodePurchaseImg(String imgName) {
        if (imgName == null || imgName.isEmpty() || imgName.equals("null"))
            return "";
        return encodeImage(STOCK_DIR, imgName);
    }


    private static String encodeImage(String dirFile, String imgName) {
        File file = new File(dirFile + imgName);
        if (!file.exists()) {
            return "";
        }
        ByteArrayOutputStream out = null;
        Bitmap bitmap = null;
        byte[] encodeBs = null;
        try {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            byte[] bs = out.toByteArray();
            encodeBs = Base64.encode(bs, Base64.DEFAULT);
        } catch (Exception e) {
            LogUtil.w("encodeImage error");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
        if (encodeBs != null) {
            return new String(encodeBs);
        } else {
            return "";
        }
    }

    /**
     * 获取本地交易(库进出)图片路径
     */
    public static String getImgDir() {
        String dir = DATA_PIC_DIR;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dir;
    }

    /**
     * 获取图片名称
     */
    public static String getImgName() {
        return System.currentTimeMillis() + ".jpg";
    }

    /**
     * 删除本地交易图片
     */
    public static boolean deleteOrderImg(String imgName) {
        return !(imgName == null || imgName.isEmpty() || imgName.equals("null")) && deleteImg
                (DATA_PIC_DIR, imgName);
    }

    private static boolean deleteImg(String dirPath, String imgName) {
        boolean isSuccess = false;
        File file = new File(dirPath + imgName);
        if (file.exists()) {
            isSuccess = file.delete();
        } else {
            LogUtil.d(imgName + " not exists");
        }
        LogUtil.d("delete img : " + imgName + ",结果 : " + isSuccess);
        return isSuccess;
    }

    /**
     * 删除进货明细图
     */
    public static boolean deletePurchaseImg(String imgName) {
        return !(imgName == null || imgName.isEmpty() || imgName.equals("null")) && deleteImg
                (STOCK_DIR, imgName);
    }

    /**
     * 删除本地屏幕校准文件
     */
    public static boolean deleteCalibrationFile() {
        File file = new File(CALIBRATION_PATH);
        return file.exists() && file.delete();

    }

    public static LocalFileTag prepareConfigDir() {
        File file = new File("/data/seray/config");
        LocalFileTag tag = new LocalFileTag();
        if (!file.exists()) {
            if (!file.mkdirs()) {
                tag.setSuccess(false);
                tag.setContent(file.getAbsolutePath() + " create failed");
            }
        } else {
            tag.setSuccess(true);
        }
        return tag;
    }

    /**
     * 读取device.cfg文件内容
     */
    public static LocalFileTag readDeviceCode() {
        return readContent(new File(DEVICE_PATH));
    }

    /**
     * 读取文件内容
     */
    private static LocalFileTag readContent(File file) {
        LocalFileTag tag = new LocalFileTag();
        String content;
        FileInputStream is = null;
        tag.setSuccess(false);
        if (file.exists()) {
            try {
                is = new FileInputStream(file);
                int offset = 0, len;
                byte[] bs = new byte[1024 * 8];
                while ((len = is.read(bs, offset, bs.length - offset)) != -1) {
                    offset += len;
                    if (offset >= bs.length) {
                        byte[] temp = new byte[bs.length * 2];
                        System.arraycopy(bs, 0, temp, 0, offset);
                        bs = temp;
                    }
                }
                content = new String(bs, 0, offset);
                tag.setObj(content);
                tag.setSuccess(true);
                LogUtil.d("read " + file.getAbsolutePath() + "[" + content + "] success");
            } catch (Exception e) {
                tag.setSuccess(false);
                tag.setContent(e.getMessage());
                LogUtil.e(e.getMessage());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        tag.setSuccess(false);
                        tag.setContent(e.getMessage());
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }
        return tag;
    }

    /**
     * 写入update.txt文件内容
     */
    public static LocalFileTag writeToUpdate(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        return writeContent(createNewFile(UPDATE_CHECK_PATH), content);
    }

    /**
     * 写入文本内容
     */
    private static LocalFileTag writeContent(File file, String content) {
        FileOutputStream os = null;
        LocalFileTag tag = new LocalFileTag();
        try {
            os = new FileOutputStream(file);
            os.write(content.getBytes());
            os.flush();
            tag.setSuccess(true);
            LogUtil.d("write [" + content + "] to " + file.getAbsolutePath() + " success");
        } catch (Exception e) {
            tag.setContent(e.getMessage());
            LogUtil.e(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    tag.setSuccess(false);
                    tag.setContent(e.getMessage());
                    LogUtil.e(e.getMessage());
                }
            }
        }
        return tag;
    }

    /**
     * 创建文件及父文件夹
     */
    private static File createNewFile(String fileName) {
        File file = new File(fileName);
        File parFile = file.getParentFile();
        if (!parFile.exists()) {
            if (parFile.mkdirs())
                LogUtil.d("create new dir file " + parFile.getAbsolutePath());
        }
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    LogUtil.d("create new file " + file.getAbsolutePath());
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
            }
        }
        return file;
    }

    /**
     * 读取update.txt文件内容
     */
    public static LocalFileTag readUpdateContent() {
        return readContent(new File(UPDATE_CHECK_PATH));
    }

    /**
     * 用于判断是否是刚烧录的电子秤
     */
    public static boolean isSerayDirExists() {
        File file = new File(REPRINT_PATH);
        File dirFile = file.getParentFile();
        return dirFile.exists();
    }
// TODO: 2017/11/28  
    /**
     * 写入重印备份文件  
     */
//    public static LocalFileTag writeToReprint(OrderInfo info) {
//        return writeObject(createNewFile(REPRINT_PATH), info);
//    }

    /**
     * 写对象至指定文件
     */
    private static LocalFileTag writeObject(File file, Object obj) {
        ObjectOutputStream oos = null;
        LocalFileTag tag = new LocalFileTag();
        tag.setSuccess(false);
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(obj);
            oos.flush();
            tag.setSuccess(true);
            LogUtil.d(obj == null ? "null" : obj.toString() + " write to " + file.getAbsolutePath
                    () + " success");
        } catch (IOException e) {
            tag.setContent(e.getMessage());
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    tag.setSuccess(false);
                    tag.setContent(e.getMessage());
                }
            }
        }
        return tag;
    }

    /**
     * 读取重印备份文件内容
     */
    public static LocalFileTag readReprintContent() {
        return readObject(new File(REPRINT_PATH));
    }

    /**
     * 读取对象文件
     */
    private static LocalFileTag readObject(File file) {
        ObjectInputStream ois = null;
        Object obj;
        LocalFileTag tag = new LocalFileTag();
        tag.setSuccess(false);
        if (file.exists()) {
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                obj = ois.readObject();
                tag.setObj(obj);
                tag.setSuccess(true);
                LogUtil.d("read Object[" + obj.toString() + "] from " + file.getAbsolutePath() +
                        " success");
            } catch (Exception e) {
                tag.setContent(e.getMessage());
                LogUtil.e(e.getMessage());
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        tag.setContent(e.getMessage());
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }
        return tag;
    }
// TODO: 2017/11/28
    /**
     * 写入POS设备信息
     */
//    public static LocalFileTag writeToPosDevice(DeviceInfo info) {
//        return writeObject(createNewFile(POS_DEVICE_PATH), info);
//    }

    /**
     * 读取POS设备信息
     */
    public static LocalFileTag readPosDevice() {
        return readObject(new File(POS_DEVICE_PATH));
    }

    /**
     * 写入用户选择的商品信息
     */
    public static LocalFileTag writeToProducts(List<Products> list) {
        LocalFileTag tag = null;
        if (!list.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).getProductName()).append(";");
            }
            String content = sb.toString();
            content = content.substring(0, content.length() - 1);
            tag = writeContent(createNewFile(SEL_PRODUCT_PATH), content);
        }
        return tag;
    }

    /**
     * 读取用户选择的商品信息
     */
    public static LocalFileTag readSelectedProducts() {
        return readContent(new File(SEL_PRODUCT_PATH));
    }

    /**
     * 写入常用客户信息
     */
    public static LocalFileTag writeToCustomer(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        return writeContent(createNewFile(CUSTOMER_PATH), content);
    }

    /**
     * 读取常用客户信息
     */
    public static LocalFileTag readCustomer() {
        return readContent(new File(CUSTOMER_PATH));
    }

    /**
     * 写入用户搜索商品
     */
    public static LocalFileTag writeToSearch(List<Products> list) {
        return writeObject(createNewFile(SEARCH_PRODUCT_PATH), list);
    }

    /**
     * 读取用户搜索商品
     */
    public static LocalFileTag readSearchProducts() {
        return readObject(new File(SEARCH_PRODUCT_PATH));
    }

    /**
     * 写入测试Log日志
     */
    public static LocalFileTag writeToLog(String content) {
        File file = createNewFile(LOG_PATH);
        LocalFileTag tag = new LocalFileTag();
        tag.setSuccess(false);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file, true);
            os.write(content.getBytes());
            os.flush();
            tag.setSuccess(true);
        } catch (Exception e) {
            tag.setContent(e.getMessage());
            LogUtil.e(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    tag.setSuccess(false);
                    tag.setContent(e.getMessage());
                    LogUtil.e(e.getMessage());
                }
            }
        }
        return tag;
    }

    /**
     * 获取文件夹大小
     * 建议在子线程中执行
     */
    public static long getFolderSize(File dirFile) {
        long size = 0;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                size += getFolderSize(file);
            else
                size += file.length();
        }
        return size;
    }

    /**
     * 获取当前SDCard可用内存大小
     */
    public static BigDecimal getSDCardUsableSpace() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return BigDecimal.ZERO;
        }
        File storageDirectory = Environment.getExternalStorageDirectory();
        long usableSpace = storageDirectory.getUsableSpace();
        long formatSpace = usableSpace / 1024 / 1024;//change to MB
        return new BigDecimal(Long.toString(formatSpace)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 删除指定路径下的文件夹
     */
    public static boolean deleteDir(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                String root = file.getAbsolutePath();
                deleteDir(root);
            }
            return dirFile.delete();
        }
    }

    /**
     * 删除规定年份月份下文件内的图片
     */
    public static boolean deleteDir(int year, int month) {
        String dirPath = DATA_PIC_DIR + Integer.toString(year) + "/" + Integer.toString(month) + "/";
        return deleteDir(dirPath);
    }
}
