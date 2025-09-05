package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import br.com.pointel.jarch.mage.WizString;

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
        return indexData.getIndexedByName("/.");
    }

    private Pair<String, String> makeWordsAndLikes(File file) throws Exception {
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var source = new DochReader(file).read();
        var keyWords = WizString.getWordsKeySetOrdered(source);
        keyWords.addAll(WizString.getWordsKeySetOrdered(file.getAbsolutePath()));
        var words = " " + String.join(" ", keyWords) + " ";
        var keyWordsLike = WizString.getWordsLikeKeySetOrdered(source);
        keyWordsLike.addAll(WizString.getWordsLikeKeySetOrdered(file.getAbsolutePath()));
        var likes = " " + String.join(" ", keyWordsLike) + " ";
        indexData.putFile(file.getName(), words, likes, file.lastModified());
        return Pair.of(words, likes);
    }

    public String getWords(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName(file.getName());
        if (indexed == null || indexed < file.lastModified()) {
            return makeWordsAndLikes(file).getLeft();
        }
        return indexData.getWordsByName(file.getName());
    }

    public String getLikes(File file) throws Exception {
        if (!isInRoot(file)) {
            throw new Exception("The file is not in the root.");
        }
        var folder = file.getParentFile();
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName(file.getName());
        if (indexed == null || indexed < file.lastModified()) {
            return makeWordsAndLikes(file).getRight();
        }
        return indexData.getLikesByName(file.getName());
    }

    private Pair<String, String> makeWordsAndLikesFolder(File folder) throws Exception {
        var indexData = getIndexData(folder);
        var sourceWords = new StringBuilder();
        var sourceLikes = new StringBuilder();
        var biggerLastModified = 0L;
        for (var inside : folder.listFiles()) {
            if (inside.isFile() && !ArchUtils.isArchFile(inside)) {
                sourceWords.append(" ");
                sourceWords.append(getWords(inside));
                sourceLikes.append(" ");
                sourceLikes.append(getLikes(inside));
                if (inside.lastModified() > biggerLastModified) {
                    biggerLastModified = inside.lastModified();
                }
            }
        }
        var keyWords = WizString.getWordsKeySetOrdered(sourceWords.toString());
        keyWords.addAll(WizString.getWordsKeySetOrdered(folder.getAbsolutePath()));
        var words = " " + String.join(" ", keyWords) + " ";
        var keyWordsLike = WizString.getWordsLikeKeySetOrdered(sourceLikes.toString());
        keyWordsLike.addAll(WizString.getWordsLikeKeySetOrdered(folder.getAbsolutePath()));
        var likes = " " + String.join(" ", keyWordsLike) + " ";
        indexData.putFile("/.", words, likes, biggerLastModified);
        return Pair.of(words, likes);
    }

    public String getWordsFolder(File folder) throws Exception {
        if (!isInRoot(folder)) {
            throw new Exception("The folder is not in the root.");
        }
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName("/.");
        if (indexed == null) {
            return makeWordsAndLikesFolder(folder).getLeft();
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
            return makeWordsAndLikesFolder(folder).getLeft();
        }
        return indexData.getWordsByName("/.");
    }

    public String getLikesFolder(File folder) throws Exception {
        if (!isInRoot(folder)) {
            throw new Exception("The folder is not in the root.");
        }
        var indexData = getIndexData(folder);
        var indexed = indexData.getIndexedByName("/.");
        if (indexed == null) {
            return makeWordsAndLikesFolder(folder).getRight();
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
            return makeWordsAndLikesFolder(folder).getRight();
        }
        return indexData.getLikesByName("/.");
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
        indexData.delFile("/.");
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
