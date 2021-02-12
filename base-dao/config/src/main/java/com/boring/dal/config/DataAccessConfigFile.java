package com.boring.dal.config;

import java.util.ArrayList;
import java.util.List;

public class DataAccessConfigFile {

    public MasterAccess cache=new MasterAccess();
    private List<DataEntry> data;
    private ScannedModel objects;

    public List<DataEntry> getData() {
        return data;
    }

    public void setData(ArrayList<DataEntry> data) {
        this.data = data;
    }

    public ScannedModel getObjects() {
        return objects;
    }

    public void setObjects(ScannedModel objects) {
        this.objects = objects;
    }
}
