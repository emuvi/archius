package br.com.pointel.archius;

import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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

    public BufferedImage getPageAsImage(int pageNumber, int dpi) throws Exception {
        try (var doc = PDDocument.load(file)) {
            if (pageNumber < 0 || pageNumber >= doc.getNumberOfPages()) {
                throw new IllegalArgumentException("Page number " + pageNumber + " is out of bounds. The PDF has " + doc.getNumberOfPages() + " pages.");
            }
            PDFRenderer pdfRenderer = new PDFRenderer(doc);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNumber, dpi);
            return bufferedImage;
        }
    } 

}
