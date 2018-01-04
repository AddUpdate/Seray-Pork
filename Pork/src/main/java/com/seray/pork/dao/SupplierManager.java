package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.Supplier;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by pc on 2017/12/6.
 */

public class SupplierManager {
    private static final String TAG = SupplierManager.class.getSimpleName();
    private static SupplierManager manage = null;
    private DaoManager mManager;

    private SupplierManager(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }
    public static SupplierManager getInstance() {
        if (manage == null) {
            synchronized (SupplierManager.class) {
                if (manage == null) {
                    manage = new SupplierManager(App.getApplication());
                }
            }
        }
        return manage;
    }
    /**
     * 完成Supplier记录的插入，如果表未创建，先创建Supplier表
     * @param supplier
     * @return
     */
    public boolean insertSupplier(Supplier supplier){
        boolean flag = false;
        flag = mManager.getDaoSession().getSupplierDao().insert(supplier) == -1 ? false : true;
        Log.i(TAG, "insert Supplier :" + flag + "-->" + supplier.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param supplierList
     * @return
     */
    public boolean insertMultSupplier(final List<Supplier> supplierList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Supplier supplier : supplierList) {
                        mManager.getDaoSession().insertOrReplace(supplier);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param supplier
     * @return
     */
    public boolean updateSupplier(Supplier supplier){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(supplier);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param supplier
     * @return
     */
    public boolean deleteSupplier(Supplier supplier){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(supplier);
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
            mManager.getDaoSession().deleteAll(Supplier.class);
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
    public List<Supplier> queryAllSupplier(){
        return mManager.getDaoSession().loadAll(Supplier.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Supplier querySupplierById(long key){
        return mManager.getDaoSession().load(Supplier.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Supplier> querySupplierByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Supplier.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Supplier> querySupplierByQueryBuilder(long id){
        QueryBuilder<Supplier> queryBuilder = mManager.getDaoSession().queryBuilder(Supplier.class);
        return queryBuilder.where(SupplierDao.Properties.Id.eq(id)).list();
    }
    
}
