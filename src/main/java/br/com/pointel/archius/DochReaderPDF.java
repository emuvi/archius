package br.com.pointel.archius;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
            var count = doc.getNumberOfPages();
            var builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                if (i > 0) {
                    builder.append(System.lineSeparator());
                    builder.append("---");
                    builder.append(System.lineSeparator());
                    builder.append(System.lineSeparator());
                }
                builder.append(readPage(doc, i));
            }
            return builder.toString();
        }
    }

    public Integer countPages() throws Exception {
        try (var doc = PDDocument.load(file)) {
            return doc.getNumberOfPages();
        }
    }

    public String readPage(int pageNumber) throws Exception {
        try (var doc = PDDocument.load(file)) {
            return readPage(doc, pageNumber);
        }
    }

    public static String readPage(PDDocument doc, int pageNumber) throws IOException {
        int numPages = doc.getNumberOfPages();
        if (pageNumber < 0 || pageNumber >= numPages) {
            throw new IllegalArgumentException("Page number " + pageNumber + " is out of bounds. The PDF has " + numPages + " pages.");
        }
        var stripper = new PDFTextStripper();
        stripper.setStartPage(pageNumber + 1);
        stripper.setEndPage(pageNumber + 1);
        String text = stripper.getText(doc).trim();

        var page = doc.getPage(pageNumber);
        var annotations = page.getAnnotations();
        StringBuilder comments = new StringBuilder();
        for (var annotation : annotations) {
            var contents = annotation.getContents();
            if (contents != null && !contents.isBlank()) {
                if (comments.length() > 0) {
                    comments.append(System.lineSeparator());
                    comments.append("--:");
                    comments.append(System.lineSeparator());
                    comments.append(System.lineSeparator());
                }
                comments.append(contents.trim());
            }
        }

        if (comments.length() > 0) {
            text = text
                            + System.lineSeparator()
                            + "--\\" 
                            + System.lineSeparator()
                            + System.lineSeparator()
                            + comments.toString();
        }
        return text;
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
