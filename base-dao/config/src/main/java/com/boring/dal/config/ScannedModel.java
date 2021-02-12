package com.boring.dal.config;

import java.util.ArrayList;

public class ScannedModel {

    private ArrayList<String> scanPkg;

    private ArrayList<String> defined;

    public ArrayList<String> getScanPkg() {
        return scanPkg;
    }

    public void setScanPkg(ArrayList<String> scanPkg) {
        this.scanPkg = scanPkg;
    }

    public ArrayList<String> getDefined() {
        return defined;
    }

    public void setDefined(ArrayList<String> defined) {
        this.defined = defined;
    }
}
