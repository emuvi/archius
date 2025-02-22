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

    private static final Integer DEFAULT_SPEED = 8;

    private final ArchBase archBase;
    private final Integer speed;

    private final Deque<File> files;

    private final AtomicBoolean shouldStop;
    private final AtomicBoolean doneLoadFiles;
    private final AtomicInteger doneLoadVerifiers;
    private final AtomicBoolean doneLinterClean;

    private final AtomicInteger statusProgressPos;
    private final AtomicInteger statusProgressMax;

    private final AtomicInteger statusNumberOfFiles;
    private final AtomicInteger statusNumberOfChecked;
    private final AtomicInteger statusNumberOfVerified;
    private final AtomicInteger statusNumberOfCleaned;
    private final AtomicInteger statusNumberOfErros;

    public ArchBaseLoad(ArchBase archBase) throws Exception {
        this(archBase, DEFAULT_SPEED);
    }

    public ArchBaseLoad(ArchBase archBase, Integer speed) throws Exception {
        this.archBase = archBase;
        this.speed = speed;
        this.files = new ConcurrentLinkedDeque<>();
        this.shouldStop = new AtomicBoolean(false);
        this.doneLoadFiles = new AtomicBoolean(false);
        this.doneLoadVerifiers = new AtomicInteger(0);
        this.doneLinterClean = new AtomicBoolean(false);
        this.statusProgressPos = new AtomicInteger(0);
        this.statusProgressMax = new AtomicInteger(0);
        this.statusNumberOfFiles = new AtomicInteger(0);
        this.statusNumberOfChecked = new AtomicInteger(0);
        this.statusNumberOfVerified = new AtomicInteger(0);
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
        for (int i = 1; i <= speed; i++) {
            new Thread("ArchBaseLoad - Verifier " + i) {
                @Override
                public void run() {
                    loadVerifiers();
                    doneLoadVerifiers.incrementAndGet();
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
                && doneLinterClean.get();
    }

    private boolean isDoneVerifiers() {
        return doneLoadVerifiers.get() == speed;
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
            if (!(path.getName().startsWith("arch") && path.getName().endsWith(".sdb"))) {
                files.addLast(path);
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
            var file = files.pollFirst();
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
                if (baseFile == null || !Objects.equals(file.length(), baseFile.getModified())) {
                    try (FileInputStream input = new FileInputStream(file)) {
                        var verifier = DigestUtils.sha256Hex(input);
                        archBase.putFile(place, verifier, file.length());
                        archBase.sendToListeners("Putted: " + file.getName());
                        this.statusNumberOfVerified.incrementAndGet();
                    }
                }
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
                if (this.shouldStop.get()) {
                    return;
                }
                try {
                    var file = new File(archBase.getRoot(), place);
                    if (!file.exists()) {
                        archBase.sendToListeners("Cleaning: " + place);
                        archBase.delFile(place);
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
