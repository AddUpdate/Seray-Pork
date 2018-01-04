package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.Config;
import com.seray.entity.Products;
import com.seray.entity.PurchaseSubtotal;
import com.seray.pork.App;
import com.seray.utils.LogUtil;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/12/31.
 */

public class ConfigManager {
    private static final String TAG = ConfigManager.class.getSimpleName();
    private static ConfigManager manage = null;
    private DaoManager mManager;

    private ConfigManager(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public static ConfigManager getInstance() {
        if (manage == null) {
            synchronized (ConfigManager.class) {
                if (manage == null) {
                    manage = new ConfigManager(App.getApplication());
                }
            }
        }
        return manage;
    }

    /**
     * 完成Supplier记录的插入，如果表未创建，先创建Config表
     *
     * @param config
     * @return
     */
    public boolean insertConfig(Config config) {
        boolean flag = false;
        ConfigDao configDao = mManager.getDaoSession().getConfigDao();
        Config mConfig;
        try {
            mConfig = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(config.getKey())).uniqueOrThrow();
                mConfig.setValue(config.getValue());
                configDao.update(mConfig);
                LogUtil.e("config1", config.toString());
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            flag = configDao.insert(config) == -1 ? false : true;
            Log.i(TAG, "insert Config :" + flag + "--====>" + config.toString());
        }
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @param productsList
     * @return
     */
    public boolean insertMultProducts(final List<Config> productsList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < productsList.size(); i++) {
                        mManager.getDaoSession().insert(productsList.get(i));
                        LogUtil.e(TAG, "insert Config :" + "-->" + (productsList.get(i).toString()));
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "insert Config :" + flag);
        return flag;
    }

    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public List<Config> queryConfig(String key) {
        QueryBuilder<Config> queryBuilder = mManager.getDaoSession().queryBuilder(Config.class);
        return queryBuilder.where(ConfigDao.Properties.Key.eq(key)).list();
    }

    /**
     * 查询单条数据
     */
    public String query(String key) {
        ConfigDao configDao = mManager.getDaoSession().getConfigDao();
        Config mConfig = null;
        Database db = configDao.getDatabase();

        db.beginTransaction();
        try {
            mConfig = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(key)).uniqueOrThrow();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
        return mConfig != null ?  mConfig.getValue():"";
    }


//    private static final String TAG = "DbHelper";
//    private static ConfigManager manage = null;
//    private DaoManager mManager;
//
//    private ConfigManager(Context context) {
//        mManager = DaoManager.getInstance();
//        mManager.init(context);
//    }
//
//    public static ConfigManager getInstance() {
//        if (manage == null) {
//            synchronized (ConfigManager.class) {
//                if (manage == null) {
//                    manage = new ConfigManager(App.getApplication());
//                }
//            }
//        }
//        return manage;
//    }
//
//    /**
//     * 多条数据插入或修改
//     */
//    public void insert(final List<Config> configList) {
////        DaoSession session = mManager.getDaoSession();
////        ConfigDao configDao = session.getConfigDao();
////        Database db = configDao.getDatabase();
////
////        db.beginTransaction();
////        try {
////            for (int i = 0; i < configList.size(); i++) {
////            //    Config config = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(configList.get(i).getKey())).uniqueOrThrow();
////              //  if (config == null) {
////                    configDao.insert(configList.get(i));
////                    LogUtil.d(TAG,"insert");
//////                } else {
//////                    configDao.update(configList.get(i));
//////                }
////            }
////            db.setTransactionSuccessful();
////        } catch (Exception e) {
////            LogUtil.d(TAG, e.getMessage());
////        } finally {
////            db.endTransaction();
////        }
//        try {
//            mManager.getDaoSession().runInTx(new Runnable() {
//                @Override
//                public void run() {
//                    for (Config supplier : configList) {
//                        mManager.getDaoSession().insertOrReplace(supplier);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            LogUtil.d(TAG, e.getMessage());
//        }
//    }
//
//    /**
//     * 单条数据插入或修改
//     */
//    public void insert(Config config) {
//        ConfigDao configDao = mManager.getDaoSession().getConfigDao();
//        Database db = configDao.getDatabase();
//
//        db.beginTransaction();
//        try {
//            Config mConfig = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(config.getKey())).uniqueOrThrow();
//            if (mConfig == null) {
//                configDao.insert(config);
//            } else {
//                configDao.update(config);
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            LogUtil.d(TAG, e.getMessage());
//        } finally {
//            db.endTransaction();
//        }
//    }
//
//    /**
//     * 查询单条数据
//     */
//    public String query(String key) {
//        ConfigDao configDao = mManager.getDaoSession().getConfigDao();
//        Config mConfig = null;
//        Database db = configDao.getDatabase();
//
//        db.beginTransaction();
//        try {
//            mConfig = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(key)).uniqueOrThrow();
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            LogUtil.d(TAG, e.getMessage());
//        } finally {
//            db.endTransaction();
//        }
//        return mConfig == null ? "" : mConfig.getValue();
//    }
//
//    /**
//     * 查询多条数据
//     */
//    public List<Config> query(List<Config> configList) {
//        ConfigDao configDao = mManager.getDaoSession().getConfigDao();
//        Database db = configDao.getDatabase();
//
//        db.beginTransaction();
//        List<Config> list = new ArrayList<>();
//        for (int i = 0; i < configList.size(); i++) {
//           try {
//               Config mConfig = configDao.queryBuilder().where(ConfigDao.Properties.Key.eq(configList.get(i).getKey())).uniqueOrThrow();
//               if (mConfig != null) {
//                   list.add(mConfig);
//               }
//               db.setTransactionSuccessful();
//           } catch (Exception e) {
//               LogUtil.d(TAG, e.getMessage());
//           } finally {
//           //    db.endTransaction();
//           }
//        }
//        return list;
//    }
}
