package br.com.pointel.archius;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArchIndex {

    private final ArchBase archBase;

    public ArchIndex(ArchBase archBase) throws Exception {
        this.archBase = archBase;
    }

    public void makeIndex(File path) throws Exception {
        Thread.sleep(3000);
    }

    public void delIndex(File path) throws Exception {
        Thread.sleep(1000);
    }

    public List<File> searchFor(String words) throws Exception {
        var results = new ArrayList<File>();
        return results;
    }

}
