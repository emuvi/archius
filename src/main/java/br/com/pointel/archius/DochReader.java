package br.com.pointel.archius;

import java.io.File;

public class DochReader {

    public static boolean canRead(File file) {
        return DochReaderPDF.canRead(file)
                || DochReaderMSO.canRead(file)
                || DochReaderTXT.canRead(file);
    }

    private final File file;

    public DochReader(File file) {
        this.file = file;
    }

    public String read() throws Exception {
        if (DochReaderPDF.canRead(file)) {
            return new DochReaderPDF(file).read();
        } else if (DochReaderMSO.canRead(file)) {
            return new DochReaderMSO(file).read();
        } else if (DochReaderTXT.canRead(file)) {
            return new DochReaderTXT(file).read();
        } else {
            throw new Exception("Can not read this file type.");
        }
    }

}
