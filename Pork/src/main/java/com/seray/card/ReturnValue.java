package com.seray.card;


import com.seray.utils.NumFormatUtil;

public class ReturnValue {

    private boolean isSuccess;

    private String code;

    private String remark;

    private String cardId;

    private Object tag;

    public ReturnValue(boolean isSuccess, String code, Object tag) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.tag = tag;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIntCode() {
        if (NumFormatUtil.isInt(this.code)) {
            return Integer.parseInt(this.code);
        }
        return -999;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "ReturnValue{" +
                "isSuccess=" + isSuccess +
                ", code='" + code + '\'' +
                ", remark='" + remark + '\'' +
                ", tag=" + tag +
                '}';
    }
}
