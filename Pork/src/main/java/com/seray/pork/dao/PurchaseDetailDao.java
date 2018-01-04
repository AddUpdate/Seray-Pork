package com.seray.pork.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.seray.entity.PurchaseDetail;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PURCHASE_DETAIL".
*/
public class PurchaseDetailDao extends AbstractDao<PurchaseDetail, Long> {

    public static final String TABLENAME = "PURCHASE_DETAIL";

    /**
     * Properties of entity PurchaseDetail.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OwnerId = new Property(1, long.class, "ownerId", false, "OWNER_ID");
        public final static Property WeightId = new Property(2, int.class, "weightId", false, "WEIGHT_ID");
        public final static Property ProductId = new Property(3, String.class, "productId", false, "PRODUCT_ID");
        public final static Property ParentsItemName = new Property(4, String.class, "ParentsItemName", false, "PARENTS_ITEM_NAME");
        public final static Property ProductName = new Property(5, String.class, "productName", false, "PRODUCT_NAME");
        public final static Property PluCode = new Property(6, String.class, "pluCode", false, "PLU_CODE");
        public final static Property UnitPrice = new Property(7, float.class, "unitPrice", false, "UNIT_PRICE");
        public final static Property Quantity = new Property(8, float.class, "quantity", false, "QUANTITY");
        public final static Property Number = new Property(9, int.class, "number", false, "NUMBER");
        public final static Property ActualWeight = new Property(10, float.class, "ActualWeight", false, "ACTUAL_WEIGHT");
        public final static Property ActualNumber = new Property(11, int.class, "actualNumber", false, "ACTUAL_NUMBER");
        public final static Property CertificateImg = new Property(12, String.class, "certificateImg", false, "CERTIFICATE_IMG");
        public final static Property SellQuantity = new Property(13, float.class, "sellQuantity", false, "SELL_QUANTITY");
        public final static Property Unit = new Property(14, String.class, "unit", false, "UNIT");
        public final static Property Remark = new Property(15, String.class, "remark", false, "REMARK");
        public final static Property State = new Property(16, int.class, "state", false, "STATE");
        public final static Property Submit = new Property(17, int.class, "submit", false, "SUBMIT");
    }


    public PurchaseDetailDao(DaoConfig config) {
        super(config);
    }
    
    public PurchaseDetailDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PURCHASE_DETAIL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"OWNER_ID\" INTEGER NOT NULL ," + // 1: ownerId
                "\"WEIGHT_ID\" INTEGER NOT NULL ," + // 2: weightId
                "\"PRODUCT_ID\" TEXT," + // 3: productId
                "\"PARENTS_ITEM_NAME\" TEXT," + // 4: ParentsItemName
                "\"PRODUCT_NAME\" TEXT," + // 5: productName
                "\"PLU_CODE\" TEXT," + // 6: pluCode
                "\"UNIT_PRICE\" REAL NOT NULL ," + // 7: unitPrice
                "\"QUANTITY\" REAL NOT NULL ," + // 8: quantity
                "\"NUMBER\" INTEGER NOT NULL ," + // 9: number
                "\"ACTUAL_WEIGHT\" REAL NOT NULL ," + // 10: ActualWeight
                "\"ACTUAL_NUMBER\" INTEGER NOT NULL ," + // 11: actualNumber
                "\"CERTIFICATE_IMG\" TEXT," + // 12: certificateImg
                "\"SELL_QUANTITY\" REAL NOT NULL ," + // 13: sellQuantity
                "\"UNIT\" TEXT," + // 14: unit
                "\"REMARK\" TEXT," + // 15: remark
                "\"STATE\" INTEGER NOT NULL ," + // 16: state
                "\"SUBMIT\" INTEGER NOT NULL );"); // 17: submit
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PURCHASE_DETAIL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PurchaseDetail entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getOwnerId());
        stmt.bindLong(3, entity.getWeightId());
 
        String productId = entity.getProductId();
        if (productId != null) {
            stmt.bindString(4, productId);
        }
 
        String ParentsItemName = entity.getParentsItemName();
        if (ParentsItemName != null) {
            stmt.bindString(5, ParentsItemName);
        }
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(6, productName);
        }
 
        String pluCode = entity.getPluCode();
        if (pluCode != null) {
            stmt.bindString(7, pluCode);
        }
        stmt.bindDouble(8, entity.getUnitPrice());
        stmt.bindDouble(9, entity.getQuantity());
        stmt.bindLong(10, entity.getNumber());
        stmt.bindDouble(11, entity.getActualWeight());
        stmt.bindLong(12, entity.getActualNumber());
 
        String certificateImg = entity.getCertificateImg();
        if (certificateImg != null) {
            stmt.bindString(13, certificateImg);
        }
        stmt.bindDouble(14, entity.getSellQuantity());
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(15, unit);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(16, remark);
        }
        stmt.bindLong(17, entity.getState());
        stmt.bindLong(18, entity.getSubmit());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PurchaseDetail entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getOwnerId());
        stmt.bindLong(3, entity.getWeightId());
 
        String productId = entity.getProductId();
        if (productId != null) {
            stmt.bindString(4, productId);
        }
 
        String ParentsItemName = entity.getParentsItemName();
        if (ParentsItemName != null) {
            stmt.bindString(5, ParentsItemName);
        }
 
        String productName = entity.getProductName();
        if (productName != null) {
            stmt.bindString(6, productName);
        }
 
        String pluCode = entity.getPluCode();
        if (pluCode != null) {
            stmt.bindString(7, pluCode);
        }
        stmt.bindDouble(8, entity.getUnitPrice());
        stmt.bindDouble(9, entity.getQuantity());
        stmt.bindLong(10, entity.getNumber());
        stmt.bindDouble(11, entity.getActualWeight());
        stmt.bindLong(12, entity.getActualNumber());
 
        String certificateImg = entity.getCertificateImg();
        if (certificateImg != null) {
            stmt.bindString(13, certificateImg);
        }
        stmt.bindDouble(14, entity.getSellQuantity());
 
        String unit = entity.getUnit();
        if (unit != null) {
            stmt.bindString(15, unit);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(16, remark);
        }
        stmt.bindLong(17, entity.getState());
        stmt.bindLong(18, entity.getSubmit());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PurchaseDetail readEntity(Cursor cursor, int offset) {
        PurchaseDetail entity = new PurchaseDetail( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // ownerId
            cursor.getInt(offset + 2), // weightId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // productId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ParentsItemName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // productName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // pluCode
            cursor.getFloat(offset + 7), // unitPrice
            cursor.getFloat(offset + 8), // quantity
            cursor.getInt(offset + 9), // number
            cursor.getFloat(offset + 10), // ActualWeight
            cursor.getInt(offset + 11), // actualNumber
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // certificateImg
            cursor.getFloat(offset + 13), // sellQuantity
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // unit
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // remark
            cursor.getInt(offset + 16), // state
            cursor.getInt(offset + 17) // submit
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PurchaseDetail entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOwnerId(cursor.getLong(offset + 1));
        entity.setWeightId(cursor.getInt(offset + 2));
        entity.setProductId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setParentsItemName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProductName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPluCode(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUnitPrice(cursor.getFloat(offset + 7));
        entity.setQuantity(cursor.getFloat(offset + 8));
        entity.setNumber(cursor.getInt(offset + 9));
        entity.setActualWeight(cursor.getFloat(offset + 10));
        entity.setActualNumber(cursor.getInt(offset + 11));
        entity.setCertificateImg(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSellQuantity(cursor.getFloat(offset + 13));
        entity.setUnit(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setRemark(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setState(cursor.getInt(offset + 16));
        entity.setSubmit(cursor.getInt(offset + 17));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PurchaseDetail entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PurchaseDetail entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PurchaseDetail entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}