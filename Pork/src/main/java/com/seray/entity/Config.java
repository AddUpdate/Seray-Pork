package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 基础配置
 */
@Entity
public class Config {

    @Id(autoincrement = true)
    private Long id;
    private String key;
    private String value;
    @Generated(hash = 42005953)
    public Config(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }
    @Generated(hash = 589037648)
    public Config() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
