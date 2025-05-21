package br.com.pointel.archius;

import java.awt.image.BufferedImage;
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
            return "";
        }
    }

    public Integer countPages() throws Exception {
        if (DochReaderPDF.canRead(file)) {
            return new DochReaderPDF(file).countPages();
        } else if (DochReaderMSO.canRead(file)) {
            return 0;
        } else if (DochReaderTXT.canRead(file)) {
            return 0;
        } else {
            return 0;
        }
    }

    public BufferedImage getPageAsImage(int pageNumber, int dpi) throws Exception {
        if (DochReaderPDF.canRead(file)) {
            return new DochReaderPDF(file).getPageAsImage(pageNumber, dpi);
        } else if (DochReaderMSO.canRead(file)) {
            return null;
        } else if (DochReaderTXT.canRead(file)) {
            return null;
        } else {
            return null;
        }
    }

}
