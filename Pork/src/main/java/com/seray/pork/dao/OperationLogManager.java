package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.OperationLog;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by pc on 2017/12/12.
 */

public class OperationLogManager {

    private static final String TAG = "DbHelper";
    private static OperationLogManager manage = null;
    private DaoManager mManager;

    private OperationLogManager(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public static OperationLogManager getInstance() {
        if (manage == null) {
            synchronized (OperationLogManager.class) {
                if (manage == null) {
                    manage = new OperationLogManager(App.getApplication());
                }
            }
        }
        return manage;
    }

    /**
     * 完成OperationLog记录的插入，如果表未创建，先创建OperationLog表
     *
     * @param OperationLog
     * @return
     */
    public boolean insertOperationLog(OperationLog OperationLog) {
        boolean flag = false;
        flag = mManager.getDaoSession().getOperationLogDao().insert(OperationLog) == -1 ? false : true;
        Log.i(TAG, "insert OperationLog :" + flag + "-->" + OperationLog.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param OperationLogList
     * @return
     */
//    public boolean insertMultOperationLog(final List<OperationLog> OperationLogList) {
//        boolean flag = false;
//        try {
//            final DaoSession session = mManager.getDaoSession();
//            Database db = session.getDatabase();
//            db.beginTransaction();
//            try {
//                session.runInTx(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtil.d("thread", Thread.currentThread().getName());
//                        for (OperationLog OperationLog : OperationLogList) {
//                            session.insertOrReplace(OperationLog);
//                        }
//                    }
//                });
//                db.setTransactionSuccessful();
//            } finally {
//                flag = true;
//                db.endTransaction();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return flag;
//    }

    /**
     * 修改一条数据
     *
     * @param OperationLog
     * @return
     */
    public boolean updateOperationLog(OperationLog OperationLog) {
        boolean flag = false;
        try {
            mManager.getDaoSession().update(OperationLog);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @param OperationLog
     * @return
     */
    public boolean deleteOperationLog(OperationLog OperationLog) {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(OperationLog);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(OperationLog.class);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<OperationLog> queryAllOperationLog() {
        return mManager.getDaoSession().loadAll(OperationLog.class);
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public OperationLog queryOperationLogById(long key) {
        return mManager.getDaoSession().load(OperationLog.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<OperationLog> queryOperationLogByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(OperationLog.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询  日期区间查询
     *
     * @return
     */
    public List<OperationLog> queryOperationLogByQueryBuilder(String Date,String newData) {
        QueryBuilder<OperationLog> queryBuilder = mManager.getDaoSession().queryBuilder(OperationLog.class);
        return queryBuilder.where(OperationLogDao.Properties.Date.between(Date,newData)).list();
    }
}