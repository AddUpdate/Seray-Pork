package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;

import com.seray.entity.Library;
import com.seray.pork.App;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by pc on 2017/12/6.
 */

public class LibraryManager {
    private static final String TAG = LibraryManager.class.getSimpleName();
    private static LibraryManager manage = null;
    private DaoManager mManager;

    private LibraryManager(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }
    public static LibraryManager getInstance() {
        if (manage == null) {
            synchronized (LibraryManager.class) {
                if (manage == null) {
                    manage = new LibraryManager(App.getApplication());
                }
            }
        }
        return manage;
    }
    /**
     * 完成library记录的插入，如果表未创建，先创建Library表
     * @param library
     * @return
     */
    public boolean insertLibrary(Library library){
        boolean flag = false;
        flag = mManager.getDaoSession().getLibraryDao().insert(library) == -1 ? false : true;
        Log.i(TAG, "insert Library :" + flag + "-->" + library.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @param libraryList
     * @return
     */
    public boolean insertMultLibrary(final List<Library> libraryList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Library Library : libraryList) {
                        mManager.getDaoSession().insertOrReplace(Library);
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
     * @param Library
     * @return
     */
    public boolean updateLibrary(Library Library){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(Library);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param Library
     * @return
     */
    public boolean deleteLibrary(Library Library){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(Library);
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
            mManager.getDaoSession().deleteAll(Library.class);
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
    public List<Library> queryAllLibrary(){
        return mManager.getDaoSession().loadAll(Library.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Library queryLibraryById(long key){
        return mManager.getDaoSession().load(Library.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Library> queryLibraryByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Library.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Library> queryLibraryByQueryBuilder(long id){
        QueryBuilder<Library> queryBuilder = mManager.getDaoSession().queryBuilder(Library.class);
        return queryBuilder.where(LibraryDao.Properties.Id.eq(id)).list();
    }
}