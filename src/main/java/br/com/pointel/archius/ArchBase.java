package br.com.pointel.archius;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArchBase implements Closeable {

    private final File root;
    private final int rootLength;
    
    private final List<Consumer<String>> listeners;

    private final ArchBaseData baseData;
    private final ArchBaseLoad baseLoad;
    private final ArchIndex archIndex;

    public ArchBase(File root) throws Exception {
        if (!root.exists() || !root.isDirectory()) {
            throw new Exception("Could not find the root directory.");
        }
        this.root = root;
        this.rootLength = this.root.getAbsolutePath().length();
        this.listeners = new ArrayList<>();
        baseData = new ArchBaseData(root);
        baseLoad = new ArchBaseLoad(this);
        archIndex = new ArchIndex(this);
    }

    public File getRoot() {
        return root;
    }
    
    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }
    
    public void delListener(Consumer<String> listener) {
        listeners.remove(listener);
    }
    
    public void sendToListeners(String message) {
        for (var listener : listeners) {
            listener.accept(message);
        }
    }

    public ArchBase load() throws Exception {
        baseLoad.start();
        return this;
    }

    public Double getProgress() {
        return baseLoad.getProgress();
    }

    public String getProgressFormatted() {
        return baseLoad.getProgressFormatted();
    }

    public Integer getStatusNumberOfFiles() {
        return baseLoad.getStatusNumberOfFiles();
    }

    public Integer getStatusNumberOfChecked() {
        return baseLoad.getStatusNumberOfChecked();
    }

    public Integer getStatusNumberOfVerified() {
        return baseLoad.getStatusNumberOfVerified();
    }

    public Integer getStatusNumberOfIndexed() {
        return baseLoad.getStatusNumberOfIndexed();
    }

    public Integer getStatusNumberOfLinted() {
        return baseLoad.getStatusNumberOfLinted();
    }

    public Integer getStatusNumberOfCleaned() {
        return baseLoad.getStatusNumberOfCleaned();
    }

    public Integer getStatusNumberOfErros() {
        return baseLoad.getStatusNumberOfErros();
    }

    public boolean isInRoot(File path) {
        return path.getAbsolutePath().startsWith(root.getAbsolutePath());
    }

    public String getPlace(File path) throws Exception {
        if (!isInRoot(path)) {
            throw new Exception("The file is not in the root.");
        }
        return path.getAbsolutePath().substring(this.rootLength);
    }
    
    public String getPlaceFolder(File path) throws Exception {
        var place = getPlace(path);
        return place.substring(0, place.lastIndexOf(File.separator) + 1);
    }

    public ArchBaseUnit getByPlace(String place) throws Exception {
        return baseData.getByPlace(place);
    }

    public ArchBaseUnit getByPlace(File path) throws Exception {
        return baseData.getByPlace(getPlace(path));
    }

    public List<ArchBaseUnit> getByVerifier(String verifier) throws Exception {
        return baseData.getByVerifier(verifier);
    }

    public List<ArchBaseUnit> getAll() throws Exception {
        return baseData.getAll();
    }

    public List<String> getAllPlaces() throws Exception {
        return baseData.getAllPlaces();
    }
    
    public void putFile(String place, String verifier, Long modified) throws Exception {
        baseData.putFile(place, verifier, modified);
    }

    public void putFile(File path, String verifier) throws Exception {
        baseData.putFile(getPlace(path), verifier, path.lastModified());
    }
    
    public void putIndexed(String place, Long indexed) throws Exception {
        baseData.putIndexed(place, indexed);
    }

    public void putIndexed(File path, Long indexed) throws Exception {
        baseData.putIndexed(getPlace(path), indexed);
    }

    public void putIndexed(File path) throws Exception {
        baseData.putIndexed(getPlace(path), path.lastModified());
    }

    public void delFolder(String place) throws Exception {
        baseData.delFolder(place);
    }
    
    public void delFolder(File path) throws Exception {
        baseData.delFolder(getPlace(path));
    }

    public void delFile(String place) throws Exception {
        baseData.delFile(place);
    }
    
    public void delFile(File path) throws Exception {
        baseData.delFile(getPlace(path));
    }

    public void moveFolder(String fromPlace, String toPlace) throws Exception {
        baseData.moveFolder(fromPlace, toPlace);
    }
    
    public void moveFolder(File fromPath, File toPath) throws Exception {
        baseData.moveFolder(getPlace(fromPath), getPlace(toPath));
    }

    public void moveFile(String fromPlace, String toPlace) throws Exception {
        baseData.moveFile(fromPlace, toPlace);
    }
    
    public void moveFile(File fromPath, File toPath) throws Exception {
        baseData.moveFile(getPlace(fromPath), getPlace(toPath));
    }

    public void makeIndex(File path) throws Exception {
        archIndex.makeIndex(path);
        putIndexed(path);
    }

    public void delIndex(File path) throws Exception {
        archIndex.delIndex(path);
    }

    public List<File> searchFor(String words) throws Exception {
        return archIndex.searchFor(words);
    }

    @Override
    public void close() throws IOException {
        baseLoad.stop();
        baseData.close();
    }

}
