package com.seray.pork.dao;


import android.content.Context;
import android.support.annotation.NonNull;

import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseOrder;
import com.seray.entity.PurchaseSubtotal;
import com.seray.pork.App;
import com.seray.utils.LogUtil;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class PurchaseOrderManager {

    private static final String TAG = "DbHelper";
    private static PurchaseOrderManager manage = null;
    private DaoManager mManager;

    private PurchaseOrderManager(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public static PurchaseOrderManager getInstance() {
        if (manage == null) {
            synchronized (PurchaseOrderManager.class) {
                if (manage == null) {
                    manage = new PurchaseOrderManager(App.getApplication());
                }
            }
        }
        return manage;
    }


    public void insertOrder(@NonNull PurchaseOrder order) {

        LogUtil.d(TAG, order.toString());

        DaoSession session = mManager.getDaoSession();
        PurchaseDetailDao detailDao = session.getPurchaseDetailDao();
        PurchaseSubtotalDao subtotalDao = session.getPurchaseSubtotalDao();

        PurchaseSubtotal subtotal = order.getPurchaseSubtotal();
        List<PurchaseDetail> detailList = order.getPurchaseDetails();

        String batchNumber = subtotal.getBatchNumber();

        Database db = session.getDatabase();

        db.beginTransaction();

        try {
            subtotalDao.insert(subtotal);

            PurchaseSubtotal unique = subtotalDao.queryBuilder()
                    .where(PurchaseSubtotalDao.Properties.BatchNumber.eq(batchNumber)).uniqueOrThrow();

            Long uniqueID = unique.getId();

            LogUtil.d(TAG, "Insert A PurchaseSubtotal [ " + batchNumber + " ]");

            for (int i = 0; i < detailList.size(); i++) {

                PurchaseDetail detail = detailList.get(i);

                detail.setOwnerId(uniqueID);

                detailDao.insert(detail);

                LogUtil.d(TAG, "Insert A PurchaseDetail [ " + detail.getProductName() + " ]");
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }
}
