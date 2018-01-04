package com.seray.scalesdb;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

import com.seray.utils.FileHelp;
import com.seray.utils.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * 用于支持对存储在SD卡上的数据库的访问
 **/
public class DatabaseContext extends ContextWrapper {

    public DatabaseContext(Context base) {
        super(base);
    }

    /**
     * 打开SD卡上的数据库
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * Android 4.0会调用此方法获取数据库
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * 获得数据库路径，如果不存在，则创建
     */
    @Override
    public File getDatabasePath(String name) {
        // 判断是否存在sd卡
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!sdExist) {
            LogUtil.e("SD卡不存在");
            return null;
        } else {
            File dirFile = new File(FileHelp.DATABASE_DIR);// 数据库文件夹
            if (!dirFile.exists()) {
                try {
                    if (!dirFile.mkdirs()) {
                        LogUtil.e("创建文件目录失败" + dirFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    LogUtil.e("数据库文件夹创建失败");
                }
            }
            String dbPath = FileHelp.DATABASE_DIR + name;
            boolean isFileCreateSuccess = false;
            File dbFile = new File(dbPath);// 数据库文件
            if (!dbFile.exists()) {
                try {
                    isFileCreateSuccess = dbFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                isFileCreateSuccess = true;
            if (isFileCreateSuccess)
                return dbFile;
            else
                return null;
        }
    }
}