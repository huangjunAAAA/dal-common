package com.boring.dal.config;

public class MasterAccess {

    private int dirty=200;

    private int panic=100;

    public int getDirty() {
        return dirty;
    }

    public void setDirty(int dirty) {
        this.dirty = dirty;
    }

    public int getPanic() {
        return panic;
    }

    public void setPanic(int panic) {
        this.panic = panic;
    }
}
