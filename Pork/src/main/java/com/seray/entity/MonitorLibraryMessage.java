package com.seray.entity;

/**
 * Created by pc on 2017/12/6.
 */

public class MonitorLibraryMessage {
  Library library;

    public MonitorLibraryMessage(Library library) {
        this.library=library;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return "MonitorLibraryMessage{" +
                "library=" + library +
                '}';
    }
}
