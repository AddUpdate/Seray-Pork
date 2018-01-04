package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.ProductsCategory;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 品名大类
 */

public class ProductsCategoryManager {

    private static final String TAG = ProductsCategoryManager.class.getSimpleName();
    private static ProductsCategoryManager manage = null;
    private DaoManager mManager;

    public ProductsCategoryManager(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }
    public static ProductsCategoryManager getInstance() {
        if (manage == null) {
            synchronized (ProductsCategoryManager.class) {
                if (manage == null) {
                    manage = new ProductsCategoryManager(App.getApplication());
                }
            }
        }
        return manage;
    }
    /**
     * 完成ProductsCategory记录的插入，如果表未创建，先创建ProductsCategory表
     * @param productsCategory
     * @return
     */
    public boolean insertProductsCategory(ProductsCategory productsCategory){
        boolean flag = false;
        flag = mManager.getDaoSession().getProductsCategoryDao().insert(productsCategory) == -1 ? false : true;
        Log.i(TAG, "insert ProductsCategory :" + flag + "-->" + productsCategory.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param productsCategoryList
     * @return
     */
    public boolean insertMultProductsCategory(final List<ProductsCategory> productsCategoryList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ProductsCategory productsCategory : productsCategoryList) {
                        mManager.getDaoSession().insertOrReplace(productsCategory);
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
     * @param productsCategory
     * @return
     */
    public boolean updateProductsCategory(ProductsCategory productsCategory){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(productsCategory);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param productsCategory
     * @return
     */
    public boolean deleteProductsCategory(ProductsCategory productsCategory){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(productsCategory);
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
            mManager.getDaoSession().deleteAll(ProductsCategory.class);
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
    public List<ProductsCategory> queryAllProductsCategory(){
        return mManager.getDaoSession().loadAll(ProductsCategory.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public ProductsCategory queryProductsCategoryById(long key){
        return mManager.getDaoSession().load(ProductsCategory.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<ProductsCategory> queryProductsCategoryByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(ProductsCategory.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<ProductsCategory> queryProductsCategoryByQueryBuilder(long id){
        QueryBuilder<ProductsCategory> queryBuilder = mManager.getDaoSession().queryBuilder(ProductsCategory.class);
        return queryBuilder.where(ProductsCategoryDao.Properties.Id.eq(id)).list();
    }
}
