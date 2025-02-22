package br.com.pointel.archius;

import java.io.File;
import java.io.FileInputStream;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.codec.digest.DigestUtils;
import br.com.pointel.jarch.mage.WizBase;

public class ArchBaseLoad {

    private final ArchBase archBase;
    private final Integer loadSpeed;

    private final Deque<File> filesToVerify;
    private final Deque<File> filesToIndex;

    private final AtomicBoolean shouldStop;
    private final AtomicBoolean doneLoadFiles;
    private final AtomicInteger doneLoadVerifiers;
    private final AtomicInteger doneLoadIndexes;
    private final AtomicBoolean doneLinterClean;

    private final AtomicInteger statusProgressPos;
    private final AtomicInteger statusProgressMax;

    private final AtomicInteger statusNumberOfFiles;
    private final AtomicInteger statusNumberOfChecked;
    private final AtomicInteger statusNumberOfVerified;
    private final AtomicInteger statusNumberOfIndexed;
    private final AtomicInteger statusNumberOfLinted;
    private final AtomicInteger statusNumberOfCleaned;
    private final AtomicInteger statusNumberOfErros;

    public ArchBaseLoad(ArchBase archBase) throws Exception {
        this(archBase, Archius.DEFAULT_SPEED);
    }

    public ArchBaseLoad(ArchBase archBase, Integer loadSpeed) throws Exception {
        this.archBase = archBase;
        this.loadSpeed = loadSpeed;
        this.filesToVerify = new ConcurrentLinkedDeque<>();
        this.filesToIndex = new ConcurrentLinkedDeque<>();
        this.shouldStop = new AtomicBoolean(false);
        this.doneLoadFiles = new AtomicBoolean(false);
        this.doneLoadVerifiers = new AtomicInteger(0);
        this.doneLoadIndexes = new AtomicInteger(0);
        this.doneLinterClean = new AtomicBoolean(false);
        this.statusProgressPos = new AtomicInteger(0);
        this.statusProgressMax = new AtomicInteger(0);
        this.statusNumberOfFiles = new AtomicInteger(0);
        this.statusNumberOfChecked = new AtomicInteger(0);
        this.statusNumberOfVerified = new AtomicInteger(0);
        this.statusNumberOfIndexed = new AtomicInteger(0);
        this.statusNumberOfLinted = new AtomicInteger(0);
        this.statusNumberOfCleaned = new AtomicInteger(0);
        this.statusNumberOfErros = new AtomicInteger(0);
    }

    public ArchBaseLoad start() {
        new Thread("ArchBaseLoad - Files") {
            @Override
            public void run() {
                loadFiles(archBase.getRoot());
                doneLoadFiles.set(true);
            }
        }.start();
        for (int i = 1; i <= loadSpeed; i++) {
            new Thread("ArchBaseLoad - Verifier " + i) {
                @Override
                public void run() {
                    loadVerifiers();
                    doneLoadVerifiers.incrementAndGet();
                }
            }.start();
        }
        for (int i = 1; i <= loadSpeed; i++) {
            new Thread("ArchBaseLoad - Index " + i) {
                @Override
                public void run() {
                    loadIndexes();
                    doneLoadIndexes.incrementAndGet();
                }
            }.start();
        }
        new Thread("ArchBaseLoad - Linter") {
            @Override
            public void run() {
                makeLinterClean();
                doneLinterClean.set(true);
            }
        }.start();
        return this;
    }

    public void stop() {
        shouldStop.set(true);
        while (!isDone()) {
            WizBase.sleep(10);
        }
    }

    public Boolean isDone() {
        return doneLoadFiles.get()
                        && isDoneVerifiers()
                        && isDoneIndexes()
                        && doneLinterClean.get();
    }

    private boolean isDoneVerifiers() {
        return doneLoadVerifiers.get() == loadSpeed;
    }

    private boolean isDoneIndexes() {
        return doneLoadVerifiers.get() == loadSpeed;
    }

    public Double getProgress() {
        var pos = (double) statusProgressPos.get();
        var max = (double) statusProgressMax.get();
        return max > 0 ? pos / max * 100.0 : 0.0;
    }

    public String getProgressFormatted() {
        return String.format("%.2f%%", getProgress());
    }

    public Integer getStatusNumberOfFiles() {
        return statusNumberOfFiles.get();
    }

    public Integer getStatusNumberOfChecked() {
        return statusNumberOfChecked.get();
    }

    public Integer getStatusNumberOfVerified() {
        return statusNumberOfVerified.get();
    }

    public Integer getStatusNumberOfIndexed() {
        return statusNumberOfIndexed.get();
    }

    public Integer getStatusNumberOfLinted() {
        return statusNumberOfLinted.get();
    }

    public Integer getStatusNumberOfCleaned() {
        return statusNumberOfCleaned.get();
    }

    public Integer getStatusNumberOfErros() {
        return statusNumberOfErros.get();
    }

    private void loadFiles(File path) {
        if (shouldStop.get()) {
            return;
        }
        if (path.isFile()) {
            if (!(path.getName().startsWith("arch-") && path.getName().endsWith(".sdb"))) {
                filesToVerify.addLast(path);
                this.statusProgressMax.incrementAndGet();
                this.statusNumberOfFiles.incrementAndGet();
            }
        } else if (path.isDirectory()) {
            for (var inside : path.listFiles()) {
                loadFiles(inside);
            }
        }
    }

    private void loadVerifiers() {
        while (true) {
            if (shouldStop.get()) {
                break;
            }
            var file = filesToVerify.pollFirst();
            if (file == null) {
                if (doneLoadFiles.get()) {
                    break;
                } else {
                    WizBase.sleep(100);
                    continue;
                }
            }
            try {
                archBase.sendToListeners("Checking: " + file.getName());
                var place = archBase.getPlace(file);
                var baseFile = archBase.getByPlace(place);
                if (baseFile == null || !Objects.equals(baseFile.getModified(), file.lastModified())) {
                    try (FileInputStream input = new FileInputStream(file)) {
                        var verifier = DigestUtils.sha256Hex(input);
                        archBase.putFile(place, verifier, file.lastModified());
                        archBase.sendToListeners("Verified: " + file.getName());
                        this.statusNumberOfVerified.incrementAndGet();
                    }
                }
                var indexed = archBase.getIndexed(file);
                if (baseFile == null || indexed == null || !Objects.equals(indexed, file.lastModified())) {
                    filesToIndex.addLast(file);
                    this.statusProgressMax.incrementAndGet();
                }
                archBase.sendToListeners("Checked: " + file.getName());
                this.statusNumberOfChecked.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
                archBase.sendToListeners("Error: " + e.getMessage());
                statusNumberOfErros.incrementAndGet();
            } finally {
                this.statusProgressPos.incrementAndGet();
            }
        }
    }

    private void loadIndexes() {
        while (true) {
            if (shouldStop.get()) {
                break;
            }
            var file = filesToIndex.pollFirst();
            if (file == null) {
                if (doneLoadFiles.get() && isDoneVerifiers()) {
                    break;
                } else {
                    WizBase.sleep(100);
                    continue;
                }
            }
            try {
                archBase.sendToListeners("Indexing: " + file.getName());
                archBase.makeIndex(file);
                archBase.sendToListeners("Indexed: " + file.getName());
                this.statusNumberOfIndexed.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
                archBase.sendToListeners("Error: " + e.getMessage());
                statusNumberOfErros.incrementAndGet();
            } finally {
                this.statusProgressPos.incrementAndGet();
            }
        }
    }

    private void makeLinterClean() {
        try {
            while (!isDoneVerifiers()) {
                WizBase.sleep(100);
                if (shouldStop.get()) {
                    return;
                }
            }
            var places = archBase.getAllPlaces();
            statusProgressMax.addAndGet(places.size());
            for (var place : places) {
                statusNumberOfLinted.incrementAndGet();
                if (this.shouldStop.get()) {
                    return;
                }
                try {
                    var file = new File(archBase.getRoot(), place);
                    if (!file.exists()) {
                        archBase.sendToListeners("Cleaning: " + place);
                        archBase.delFile(place);
                        archBase.delIndex(file);
                        archBase.sendToListeners("Cleaned: " + place);
                        statusNumberOfCleaned.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    archBase.sendToListeners("Error: " + e.getMessage());
                    statusNumberOfErros.incrementAndGet();
                } finally {
                    statusProgressPos.incrementAndGet();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            archBase.sendToListeners("Error: " + e.getMessage());
            statusNumberOfErros.incrementAndGet();
        }
    }

}
