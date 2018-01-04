package com.seray.entity;

/**
 * 扣重
 */
public class TareItem {

    private int type;
    private String name;
    private int count;
    private String netStr;

    public TareItem(int type, String name, String net) {
        this.type = type;
        this.name = name;
        this.netStr = net;
        this.count = 0;
    }

    public String getNetStr() {
        return netStr;
    }

    public void setNetStr(String netStr) {
        this.netStr = netStr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TareItem{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", netStr='" + netStr + '\'' +
                '}';
    }
}
