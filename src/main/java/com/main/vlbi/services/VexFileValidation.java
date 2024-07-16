package com.main.vlbi.services;

import com.main.vlbi.vexparser.VexParser;
import com.main.vlbi.vexparser.VexScan;

import java.util.ArrayList;
import java.util.List;

public class VexFileValidation {

    public boolean validateVexFile(List<String> vex_file_lines) {
        VexParser vexParser = new VexParser("", vex_file_lines);
        ArrayList<VexScan> vexScanList;

        try {
            vexScanList = vexParser.extractSched();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        for (VexScan vexScan : vexScanList) {
            if (vexScan.equals(null)) {
                return false;
            }
        }

        return true;
    }
}
