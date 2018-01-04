package com.seray.entity;

import java.util.List;

/**
 * Created by pc on 2017/12/6.
 * 监听下发供应商
 */

public class MonitorSupplierMsg {

    List<Supplier> list;

    public MonitorSupplierMsg(List<Supplier> list) {
        this.list = list;
    }

    public List<Supplier> getList() {
        return list;
    }

    public void setList(List<Supplier> list) {
        this.list = list;
    }
}
