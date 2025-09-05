package br.com.pointel.archius;

import java.io.File;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizThread;

public class ArchSearch {

    private final ArchIndex archIndex;
    private final List<String> searchLikes;
    private final Integer searchSpeed;

    private final AtomicBoolean shouldStop;
    private final List<File> filesToSearch;
    private final Deque<File> filesFoundsPool;

    private final AtomicBoolean doneLoadFolders;
    private final AtomicInteger doneLoadSearches;

    private final AtomicInteger statusProgressPos;
    private final AtomicInteger statusProgressMax;

    private final AtomicInteger statusNumberOfFolders;
    private final AtomicInteger statusNumberOfFiles;
    private final AtomicInteger statusNumberOfFounds;
    private final AtomicInteger statusNumberOfErrors;

    public ArchSearch(ArchIndex archIndex, String searchWords) {
        this(archIndex, searchWords, Archius.DEFAULT_SPEED);
    }

    public ArchSearch(ArchIndex archIndex, String searchWords, Integer searchSpeed) {
        this.archIndex = archIndex;
        this.searchLikes = WizString.getWordsLikeKeySetOrdered(searchWords).stream()
                        .map(word -> " " + word + " ").toList();
        this.searchSpeed = searchSpeed;

        this.shouldStop = new AtomicBoolean(false);
        this.filesToSearch = new ArrayList<>();
        this.filesFoundsPool = new ConcurrentLinkedDeque<>();

        this.doneLoadFolders = new AtomicBoolean(false);
        this.doneLoadSearches = new AtomicInteger(0);

        this.statusProgressPos = new AtomicInteger(0);
        this.statusProgressMax = new AtomicInteger(0);

        this.statusNumberOfFolders = new AtomicInteger(0);
        this.statusNumberOfFiles = new AtomicInteger(0);
        this.statusNumberOfFounds = new AtomicInteger(0);
        this.statusNumberOfErrors = new AtomicInteger(0);
    }

    public File getRoot() {
        return archIndex.getRoot();
    }

    public ArchSearch start() {
        new Thread("ArchSearch - Folders") {
            @Override
            public void run() {
                loadFolders(archIndex.getRoot());
                doneLoadFolders.set(true);
            }
        }.start();
        for (int i = 1; i <= searchSpeed; i++) {
            new Thread("ArchSearch - Searches " + i) {
                @Override
                public void run() {
                    loadSearches();
                    doneLoadSearches.incrementAndGet();
                }
            }.start();
        }
        return this;
    }

    public void stop() {
        shouldStop.set(true);
    }

    public void stopAndWait() {
        stop();
        while (!isDone()) {
            WizThread.sleep(10);
        }
    }

    public Boolean isDone() {
        return doneLoadFolders.get()
                        && isDoneSearches();
    }

    public File pollFound() {
        return filesFoundsPool.pollFirst();
    }

    public Boolean gotAll() {
        return isDone() && filesFoundsPool.isEmpty();
    }

    private boolean isDoneSearches() {
        return doneLoadSearches.get() == searchSpeed;
    }

    public Double getProgress() {
        var pos = (double) statusProgressPos.get();
        var max = (double) statusProgressMax.get();
        return max > 0 ? pos / max * 100.0 : 0.0;
    }

    public String getProgressFormatted() {
        return String.format("%.2f%%", getProgress());
    }

    public Integer getStatusNumberOfFolders() {
        return statusNumberOfFolders.get();
    }

    public Integer getStatusNumberOfFiles() {
        return statusNumberOfFiles.get();
    }

    public Integer getStatusNumberOfFounds() {
        return statusNumberOfFounds.get();
    }

    public Integer getStatusNumberOfErros() {
        return statusNumberOfErrors.get();
    }

    private void loadFolders(File path) {
        if (shouldStop.get()) {
            return;
        }
        if (path.isDirectory()) {
            for (var inside : path.listFiles()) {
                if (shouldStop.get()) {
                    return;
                }
                loadFolders(inside);
            }
            this.statusNumberOfFolders.incrementAndGet();
        } else if (path.isFile() && !ArchUtils.isArchFile(path)) {
            synchronized (filesToSearch) {
                filesToSearch.addLast(path);
            }
            this.statusProgressMax.incrementAndGet();
            this.statusNumberOfFiles.incrementAndGet();
        }
    }

    private void loadSearches() {
        while (true) {
            if (shouldStop.get()) {
                break;
            }
            File file = null;
            synchronized (filesToSearch) {
                if (filesToSearch.isEmpty()) {
                    if (doneLoadFolders.get()) {
                        break;
                    }
                } else {
                    file = filesToSearch.removeFirst();
                }
            }
            if (file == null) {
                WizThread.sleep(100);
                continue;
            }
            try {
                var fileLikes = archIndex.getLikes(file);
                var found = true;
                for (var searchLike : searchLikes) {
                    if (!fileLikes.contains(searchLike)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    filesFoundsPool.addLast(file);
                    statusNumberOfFounds.incrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusNumberOfErrors.incrementAndGet();
            } finally {
                this.statusProgressPos.incrementAndGet();
            }
        }
    }

}
