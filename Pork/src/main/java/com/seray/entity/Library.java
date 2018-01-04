package com.seray.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 *  仓库
 */
@Entity
public class Library {
    @Id(autoincrement = true)
    private Long id;
    private String LibraryId;
    private String LibraryName;
    private String State;

    public Library(){}
    public Library(String libraryId,String libraryName,String state){
        this.LibraryId = libraryId;
        this.LibraryName = libraryName;
        this.State = state;
    }
    @Generated(hash = 1694802316)
    public Library(Long id, String LibraryId, String LibraryName, String State) {
        this.id = id;
        this.LibraryId = LibraryId;
        this.LibraryName = LibraryName;
        this.State = State;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLibraryId() {
        return this.LibraryId;
    }
    public void setLibraryId(String LibraryId) {
        this.LibraryId = LibraryId;
    }
    public String getLibraryName() {
        return this.LibraryName;
    }
    public void setLibraryName(String LibraryName) {
        this.LibraryName = LibraryName;
    }
    public String getState() {
        return this.State;
    }
    public void setState(String State) {
        this.State = State;
    }

}
