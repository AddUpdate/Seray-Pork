package com.seray.entity;

/**
 * 刷新主界面的PLU
 * 获取最新的产地信息
 */
public class SaoQRCodeMsg {

    private boolean isSuccess;

    private String msg;

    public SaoQRCodeMsg(boolean isSuccess, String msg) {
        this.isSuccess = isSuccess;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
