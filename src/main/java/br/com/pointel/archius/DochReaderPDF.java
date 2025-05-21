package br.com.pointel.archius;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DochReaderPDF {
    
    public static boolean canRead(File file) {
        return DochReaderPDFUtils.isPDFFile(file);
    }
    
    private final File file;

    public DochReaderPDF(File file) {
        this.file = file;
    }

    public String read() throws Exception {
        try (var doc = PDDocument.load(file)) {
            var stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(doc.getNumberOfPages());
            return stripper.getText(doc);
        }
    }

    public Integer countPages() throws Exception {
        try (var doc = PDDocument.load(file)) {
            return doc.getNumberOfPages();
        }
    }

}
