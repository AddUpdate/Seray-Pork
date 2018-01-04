package com.seray.entity;

import java.util.List;

/**
 * Created by pc on 2017/12/6.
 */

public class MonitorProdctsMessage {
    List<ProductsCategory> list;

    public MonitorProdctsMessage(List<ProductsCategory> list){
        this.list = list;
    }

    public List<ProductsCategory> getList() {
        return list;
    }

    public void setList(List<ProductsCategory> list) {
        this.list = list;
    }
}
