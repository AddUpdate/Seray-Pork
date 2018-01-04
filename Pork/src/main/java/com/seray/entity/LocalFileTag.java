package com.seray.entity;

/**
 * 用于本地文本读写返回值
 */
public class LocalFileTag {

    /**
     * 文本读写状态信息
     */
    private String content;

    /**
     * 文本读写是否成功
     */
    private boolean isSuccess;

    /**
     * 承载文本读取返回值
     */
    private Object obj;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "LocalFileTag [content=" + content + ", isSuccess=" + isSuccess + ", obj=" + obj +
                "]";
    }
}
