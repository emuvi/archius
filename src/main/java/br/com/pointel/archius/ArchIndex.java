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

    public Long getIndexedFolder(File folder) throws Exception {
        if (!isInRoot(folder)) {
            throw new Exception("The folder is not in the root.");
        }
        var indexData = getIndexData(folder);
        return indexData.getIndexedByName(".");
    }

    private String makeWords(File file) throws Exception {
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var source = new DochReader(file).read();
        var words = " " + String.join(" ", WizChars.getWordsKeySet(source)) + " ";
        indexData.putFile(file.getName(), words, file.lastModified());
        return words;
    }

    public String getWords(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName(file.getName());
        if (indexed == null || indexed < file.lastModified()) {
            return makeWords(file);
        }
        return indexData.getWordsByName(file.getName());
    }

    private String makeWordsFolder(File folder) throws Exception {
        var indexData = getIndexData(folder);
        var source = new StringBuilder();
        var biggerLastModified = 0L;
        for (var inside : folder.listFiles()) {
            if (inside.isFile() && !ArchUtils.isArchFile(inside)) {
                source.append(" ");
                source.append(getWords(inside));
                if (inside.lastModified() > biggerLastModified) {
                    biggerLastModified = inside.lastModified();
                }
            }
        }
        var words = " " + String.join(" ", WizChars.getWordsKeySet(source.toString().trim())) + " ";
        indexData.putFile(".", words, biggerLastModified);
        return words;
    }

    public String getWordsFolder(File folder) throws Exception {
        if (!isInRoot(folder)) {
            throw new Exception("The folder is not in the root.");
        }
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName(".");
        if (indexed == null) {
            return makeWordsFolder(folder);
        }
        var biggerLastModified = 0L;
        for (var inside : folder.listFiles()) {
            if (inside.isFile() && !ArchUtils.isArchFile(inside)) {
                if (inside.lastModified() > biggerLastModified) {
                    biggerLastModified = inside.lastModified();
                }
            }
        }
        if (indexed < biggerLastModified) {
            return makeWordsFolder(folder);
        }
        return indexData.getWordsByName(".");
    }

    public void delIndex(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        indexData.delFile(file.getName());
    }

    public void delIndexFolder(File folder) throws Exception {
        if (!isInRoot(folder)) {
            throw new Exception("The folder is not in the root.");
        }
        var indexData = getIndexData(folder);
        indexData.delFile(".");
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
