package com.seray.entity;

import java.util.List;

/**
 * Created by pc on 2017/12/15.
 */

public class LibraryList {
    private List<Library> libraryList;

    public List<Library> getLibraryList() {
        return libraryList;
    }

    public void setLibraryList(List<Library> libraryList) {
        this.libraryList = libraryList;
    }

    @Override
    public String toString() {
        return "LibraryList{" +
                "libraryList=" + libraryList +
                '}';
    }
}
