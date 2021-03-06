package com.seray.pork.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.seray.entity.ProductsCategory;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRODUCTS_CATEGORY".
*/
public class ProductsCategoryDao extends AbstractDao<ProductsCategory, Long> {

    public static final String TABLENAME = "PRODUCTS_CATEGORY";

    /**
     * Properties of entity ProductsCategory.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CategoryId = new Property(1, String.class, "CategoryId", false, "CATEGORY_ID");
        public final static Property CategoryName = new Property(2, String.class, "CategoryName", false, "CATEGORY_NAME");
    }


    public ProductsCategoryDao(DaoConfig config) {
        super(config);
    }
    
    public ProductsCategoryDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRODUCTS_CATEGORY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CATEGORY_ID\" TEXT," + // 1: CategoryId
                "\"CATEGORY_NAME\" TEXT);"); // 2: CategoryName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRODUCTS_CATEGORY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ProductsCategory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String CategoryId = entity.getCategoryId();
        if (CategoryId != null) {
            stmt.bindString(2, CategoryId);
        }
 
        String CategoryName = entity.getCategoryName();
        if (CategoryName != null) {
            stmt.bindString(3, CategoryName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ProductsCategory entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String CategoryId = entity.getCategoryId();
        if (CategoryId != null) {
            stmt.bindString(2, CategoryId);
        }
 
        String CategoryName = entity.getCategoryName();
        if (CategoryName != null) {
            stmt.bindString(3, CategoryName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ProductsCategory readEntity(Cursor cursor, int offset) {
        ProductsCategory entity = new ProductsCategory( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // CategoryId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // CategoryName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ProductsCategory entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCategoryId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCategoryName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ProductsCategory entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ProductsCategory entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ProductsCategory entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
