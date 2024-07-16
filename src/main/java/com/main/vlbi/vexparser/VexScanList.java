package com.main.vlbi.vexparser;

import java.util.ArrayList;

public class VexScanList {
    private ArrayList<VexScan> scanList;

    public VexScanList(ArrayList<VexScan> scanList) {
        super();
        this.scanList = new ArrayList<>(scanList);
    }
}
