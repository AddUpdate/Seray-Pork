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
    private int Type;
    private String State;

    public Library(){}
    public Library(String libraryId,String libraryName,int type,String state){
        this.LibraryId = libraryId;
        this.LibraryName = libraryName;
        this.Type = type;
        this.State = state;
    }
    @Generated(hash = 1727727992)
    public Library(Long id, String LibraryId, String LibraryName, int Type,
            String State) {
        this.id = id;
        this.LibraryId = LibraryId;
        this.LibraryName = LibraryName;
        this.Type = Type;
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

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getState() {
        return this.State;
    }
    public void setState(String State) {
        this.State = State;
    }

}
