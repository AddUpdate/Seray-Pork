package com.seray.pork.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import com.seray.entity.PurchaseDetail;
import com.seray.entity.PurchaseSubtotal;
import com.seray.pork.App;
import com.seray.utils.DecimalFormat;
import com.seray.utils.LogUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class PurchaseDetailManager {
    private static final String TAG = PurchaseDetailManager.class.getSimpleName();
    private static PurchaseDetailManager manage = null;
    private DaoManager mManager;

    private PurchaseDetailManager(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public static PurchaseDetailManager getInstance() {
        if (manage == null) {
            synchronized (PurchaseDetailManager.class) {
                if (manage == null) {
                    manage = new PurchaseDetailManager(App.getApplication());
                }
            }
        }
        return manage;
    }

    /**
     * 修改一条数据
     */
    public void updatePurchaseDetail(@NonNull String batchNumber, @NonNull String proName,
                                     @NonNull String productId, float weight,
                                     int number, int state,int submit) {
        try {

            DaoSession session = mManager.getDaoSession();

            PurchaseSubtotalDao subtotalDao = session.getPurchaseSubtotalDao();
            PurchaseDetailDao detailDao = session.getPurchaseDetailDao();

            PurchaseSubtotal unique = subtotalDao.queryBuilder()
                    .where(PurchaseSubtotalDao.Properties.BatchNumber.eq(batchNumber)).unique();

            if (unique == null)
                return;

            Long id = unique.getId();

            PurchaseDetail detail = detailDao.queryBuilder()
                    .where(PurchaseDetailDao.Properties.OwnerId.eq(id),
                            PurchaseDetailDao.Properties.ProductName.eq(proName))
                    .unique();

            if (detail == null)
                return;

            detail.setProductId(productId);

            float beforeW = detail.getActualWeight();

            beforeW += weight;

            beforeW = DecimalFormat.getRoundFloat(3, beforeW);

            LogUtil.d(TAG, "float = " + beforeW);

            detail.setActualWeight(beforeW);

            int beforeN = detail.getActualNumber();

            beforeN += number;

            detail.setActualNumber(beforeN);

            detail.setState(state);
            detail.setSubmit(submit);

            detailDao.update(detail);

        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    public PurchaseDetail queryByBatchNumber(@NonNull String number, @NonNull String name) {

        DaoSession session = mManager.getDaoSession();
        PurchaseDetailDao detailDao = session.getPurchaseDetailDao();
        PurchaseSubtotalDao subtotalDao = session.getPurchaseSubtotalDao();

        PurchaseSubtotal subtotal = subtotalDao.queryBuilder()
                .where(PurchaseSubtotalDao.Properties.BatchNumber.eq(number)).unique();

//        if (subtotal != null) {
//
//        }
        Long id = subtotal.getId();


        PurchaseDetail detail = detailDao.queryBuilder()
                .where(PurchaseDetailDao.Properties.ProductName.eq(name),
                        PurchaseDetailDao.Properties.OwnerId.eq(id)).unique();

        LogUtil.d(TAG, detail.toString());

        return detail;

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
            mManager.getDaoSession().deleteAll(PurchaseDetail.class);
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
    public List<PurchaseDetail> queryAllPurchaseDetail() {
        return mManager.getDaoSession().loadAll(PurchaseDetail.class);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<PurchaseDetail> queryPurchaseDetailByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().queryRaw(PurchaseDetail.class, sql, conditions);
    }
}
