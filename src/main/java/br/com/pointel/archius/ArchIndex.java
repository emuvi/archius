package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import br.com.pointel.jarch.mage.WizChars;

public class ArchIndex implements Closeable {

    private final File root;
    private final Map<File, ArchIndexData> indexMap;

    public ArchIndex(File root) throws Exception {
        this.root = root;
        this.indexMap = new HashMap<>();
    }

    public boolean isInRoot(File file) {
        return file.getAbsolutePath().startsWith(root.getAbsolutePath());
    }

    public Long getIndexed(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var root = file.getParentFile();
        var indexData = getIndexData(root);
        return indexData.getIndexedByName(file.getName());
    }

    public void makeIndex(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var root = file.getParentFile();
        var indexData = getIndexData(root);
        var source = new DochReader(file).read();
        var words = "|" + String.join("|", WizChars.getWords(source)) + "|";
        indexData.putFile(file.getName(), words, file.lastModified());
    }

    public void delIndex(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var root = file.getParentFile();
        var indexData = getIndexData(root);
        indexData.delFile(file.getName());
    }

    public void searchFor(String words, Consumer<File> consumer) throws Exception {
        
    }

    private ArchIndexData getIndexData(File root) throws Exception {
        synchronized (indexMap) {
            if (indexMap.containsKey(root)) {
                return indexMap.get(root);
            } else {
                var indexData = new ArchIndexData(root);
                indexMap.put(root, indexData);
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
