package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 品名类表
 */
@Entity
public class ProductsCategory {
    @Id(autoincrement = true)
    private Long id;
    private String CategoryId;
    private String CategoryName;
    @Transient
    private List<Products> productsList;

    public ProductsCategory (){}
    public ProductsCategory (String categoryId,String categoryName){
        this.CategoryId = categoryId;
        this.CategoryName = categoryName;
    }
    @Generated(hash = 241341111)
    public ProductsCategory(Long id, String CategoryId, String CategoryName) {
        this.id = id;
        this.CategoryId = CategoryId;
        this.CategoryName = CategoryName;
    }

    public List<Products> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCategoryId() {
        return this.CategoryId;
    }
    public void setCategoryId(String CategoryId) {
        this.CategoryId = CategoryId;
    }
    public String getCategoryName() {
        return this.CategoryName;
    }
    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    @Override
    public String toString() {
        return "ProductsCategory{" +
                "id=" + id +
                ", CategoryId='" + CategoryId + '\'' +
                ", CategoryName='" + CategoryName + '\'' +
                ", productsList=" + productsList +
                '}';
    }
}
