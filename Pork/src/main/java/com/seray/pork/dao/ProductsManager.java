package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.Products;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by pc on 2017/12/6.
 * 品名
 */

public class ProductsManager {
    private static final String TAG = ProductsManager.class.getSimpleName();
    private static ProductsManager manage = null;
    private DaoManager mManager;

    public ProductsManager(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }
    public static ProductsManager getInstance() {
        if (manage == null) {
            synchronized (ProductsManager.class) {
                if (manage == null) {
                    manage = new ProductsManager(App.getApplication());
                }
            }
        }
        return manage;
    }
    /**
     * 完成Products记录的插入，如果表未创建，先创建Products表
     * @param products
     * @return
     */
    public boolean insertProducts(Products products){
        boolean flag = false;
        flag = mManager.getDaoSession().getProductsDao().insert(products) == -1 ? false : true;
        Log.i(TAG, "insert Products :" + flag + "-->" + products.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param productsList
     * @return
     */
    public boolean insertMultProducts(final List<Products> productsList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Products products : productsList) {
                        mManager.getDaoSession().insertOrReplace(products);
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
     * @param products
     * @return
     */
    public boolean updateProducts(Products products){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(products);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param products
     * @return
     */
    public boolean deleteProducts(Products products){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(products);
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
            mManager.getDaoSession().deleteAll(Products.class);
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
    public List<Products> queryAllProducts(){
        return mManager.getDaoSession().loadAll(Products.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Products queryProductsById(long key){
        return mManager.getDaoSession().load(Products.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Products> queryProductsByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Products.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Products> queryProductsByQueryBuilder(String id){

        QueryBuilder<Products> queryBuilder = mManager.getDaoSession().queryBuilder(Products.class);

        return queryBuilder.where(ProductsDao.Properties.ParentId.eq(id)).list();
    }

}
