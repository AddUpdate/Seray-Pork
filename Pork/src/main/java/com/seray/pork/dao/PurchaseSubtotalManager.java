package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.PurchaseSubtotal;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class PurchaseSubtotalManager {
    private static final String TAG = PurchaseSubtotalManager.class.getSimpleName();
    private static PurchaseSubtotalManager manage = null;
    private DaoManager mManager;

    private PurchaseSubtotalManager(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }
    public static PurchaseSubtotalManager getInstance() {
        if (manage == null) {
            synchronized (PurchaseSubtotalManager.class) {
                if (manage == null) {
                    manage = new PurchaseSubtotalManager(App.getApplication());
                }
            }
        }
        return manage;
    }
    /**
     * 完成PurchaseSubtotal记录的插入，如果表未创建，先创建PurchaseSubtotal表
     * @param PurchaseSubtotal
     * @return
     */
    public boolean insertPurchaseSubtotal(PurchaseSubtotal PurchaseSubtotal){
        boolean flag = false;
        flag = mManager.getDaoSession().getPurchaseSubtotalDao().insert(PurchaseSubtotal) == -1 ? false : true;
        Log.i(TAG, "insert PurchaseSubtotal :" + flag + "-->" + PurchaseSubtotal.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param PurchaseSubtotalList
     * @return
     */
//    public boolean insertMultPurchaseSubtotal(final List<PurchaseSubtotal> PurchaseSubtotalList) {
//        boolean flag = false;
//        try {
//            mManager.getDaoSession().runInTx(new Runnable() {
//                @Override
//                public void run() {
//                    for (PurchaseSubtotal PurchaseSubtotal : PurchaseSubtotalList) {
//                        mManager.getDaoSession().insertOrReplace(PurchaseSubtotal);
//                    }
//                }
//            });
//            flag = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return flag;
//    }

    /**
     * 修改一条数据
     * @param PurchaseSubtotal
     * @return
     */
    public boolean updatePurchaseSubtotal(PurchaseSubtotal PurchaseSubtotal){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(PurchaseSubtotal);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param PurchaseSubtotal
     * @return
     */
    public boolean deletePurchaseSubtotal(PurchaseSubtotal PurchaseSubtotal){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(PurchaseSubtotal);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(PurchaseSubtotal.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<PurchaseSubtotal> queryAllPurchaseSubtotal(){
        return mManager.getDaoSession().loadAll(PurchaseSubtotal.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public PurchaseSubtotal queryPurchaseSubtotalById(long key){
        return mManager.getDaoSession().load(PurchaseSubtotal.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<PurchaseSubtotal> queryPurchaseSubtotalByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(PurchaseSubtotal.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<PurchaseSubtotal> queryPurchaseSubtotalByQueryBuilder(String id){
        QueryBuilder<PurchaseSubtotal> queryBuilder = mManager.getDaoSession().queryBuilder(PurchaseSubtotal.class);
        return queryBuilder.where(PurchaseSubtotalDao.Properties.Id.eq(id)).list();
    } 
    
}
