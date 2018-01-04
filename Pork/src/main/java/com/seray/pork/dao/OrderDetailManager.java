package com.seray.pork.dao;

import android.content.Context;
import android.util.Log;
import com.seray.entity.OrderDetail;
import com.seray.pork.App;
import com.seray.utils.LogUtil;
import java.util.List;

/**
 * Created by pc on 2017/12/27.
 */

public class OrderDetailManager {

    private static final String TAG = "DbHelper";
    private static OrderDetailManager manage = null;
    private DaoManager mManager;

    private OrderDetailManager(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    public static OrderDetailManager getInstance() {
        if (manage == null) {
            synchronized (OrderDetailManager.class) {
                if (manage == null) {
                    manage = new OrderDetailManager(App.getApplication());
                }
            }
        }
        return manage;
    }

    /**
     * 插入或替换
     * @param orderDetail
     */
    public void insertOrderDetail(OrderDetail orderDetail){
        try {

            OrderDetailDao detailDao=   mManager.getDaoSession().getOrderDetailDao();
            OrderDetail detail = detailDao.queryBuilder()
                    .where(OrderDetailDao.Properties.OrderDetailId.eq(orderDetail.getOrderDetailId())).unique();

            if (detail == null){
                detailDao.insert(orderDetail);
            }else {

                detail.setActualWeight(orderDetail.getActualWeight());

                detail.setActualNumber(orderDetail.getActualNumber());

                detail.setState(orderDetail.getState());

                detailDao.update(detail);
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        Log.i(TAG, "insert orderDetail -->" + orderDetail.toString());
    }
    /**
     * 查询所有记录
     * @return
     */
    public List<OrderDetail> queryAllProducts(){
        return mManager.getDaoSession().loadAll(OrderDetail.class);
    }
}
