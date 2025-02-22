package br.com.pointel.archius;

import java.io.File;
import java.nio.file.Files;

public class DochReaderTXT {
    
    public static boolean canRead(File file) {
        return DochReaderTXTUtils.isTXTFile(file);
    }
    
    private final File file;

    public DochReaderTXT(File file) {
        this.file = file;
    }

    public String read() throws Exception {
        return Files.readString(file.toPath());
    }

}
