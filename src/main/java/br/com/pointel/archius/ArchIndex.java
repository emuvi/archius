package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import br.com.pointel.jarch.mage.WizChars;

public class ArchIndex implements Closeable {

    private final File root;
    private final Map<File, ArchIndexData> indexMap;

    public ArchIndex(File root) throws Exception {
        this.root = root;
        this.indexMap = new HashMap<>();
    }

    public File getRoot() {
        return root;
    }

    public boolean isInRoot(File file) {
        return file.getAbsolutePath().startsWith(root.getAbsolutePath());
    }

    public Long getIndexed(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        return indexData.getIndexedByName(file.getName());
    }

    public void makeIndex(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var source = new DochReader(file).read();
        var words = "|" + String.join("|", WizChars.getWords(source)) + "|";
        indexData.putFile(file.getName(), words, file.lastModified());
    }

    public void delIndex(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        indexData.delFile(file.getName());
    }

    public ArchSearch searchFor(String words) {
        return new ArchSearch(this, words).start();
    }

    public ArchIndexData getIndexData(File folder) throws Exception {
        synchronized (indexMap) {
            if (indexMap.containsKey(folder)) {
                return indexMap.get(folder);
            } else {
                var indexData = new ArchIndexData(folder);
                indexMap.put(folder, indexData);
                return indexData;
            }
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (indexMap) {
            for (var indexDataRoot : indexMap.keySet()) {
                indexMap.get(indexDataRoot).close();
            }
            indexMap.clear();
        }
    }

}
