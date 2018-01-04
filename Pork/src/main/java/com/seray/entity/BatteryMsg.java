package com.seray.entity;

/**
 * 电池电量
 */
public class BatteryMsg {

    private int level;

    private int battery;

    private String remark;

    public BatteryMsg(int level, int battery, String remark) {
        this.level = level;
        this.battery = battery;
        this.remark = remark;
    }

    public int getLevel() {
        return level;
    }

    public int getBattery() {
        return battery;
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public String toString() {
        return "BatteryMsg{" +
                "level=" + level +
                ", battery=" + battery +
                ", remark='" + remark + '\'' +
                '}';
    }
}
