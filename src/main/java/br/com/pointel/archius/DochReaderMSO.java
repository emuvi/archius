package br.com.pointel.archius;

import java.io.File;
import java.io.FileInputStream;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DochReaderMSO {

    public static boolean canRead(File file) {
        return DochReaderMSOUtils.isMSWordFile(file)
                        || DochReaderMSOUtils.isMSExcelFile(file)
                        || DochReaderMSOUtils.isMSPowerPointFile(file);
    }


    private final File path;

    public DochReaderMSO(File path) {
        this.path = path;
    }

    public String read() throws Exception {
        if (DochReaderMSOUtils.isMSWordFile(path)) {
            return extractTextFromWord();
        } else if (DochReaderMSOUtils.isMSExcelFile(path)) {
            return extractTextFromExcel();
        } else if (DochReaderMSOUtils.isMSPowerPointFile(path)) {
            return extractTextFromPowerPoint();
        } else {
            throw new Exception("File type not expected: " + path.getName());
        }
    }

    private String extractTextFromWord() throws Exception {
        if (path.getName().toLowerCase().endsWith(".doc")) {
            return extractTextFromWordOle();
        } else if (path.getName().toLowerCase().endsWith(".docx")) {
            return extractTextFromWordXml();
        } else {
            throw new Exception("File type not expected: " + path.getName());
        }
    }

    private String extractTextFromWordOle() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var document = new HWPFDocument(fis);
                var extractor = new WordExtractor(document);) {
                return extractor.getText();
            }
        }
    }

    private String extractTextFromWordXml() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var document = new XWPFDocument(fis);
                var extractor = new XWPFWordExtractor(document);) {
                return extractor.getText();
            }
        }
    }

    private String extractTextFromExcel() throws Exception {
        if (path.getName().toLowerCase().endsWith(".xls")) {
            return extractTextFromExcelOle();
        } else if (path.getName().toLowerCase().endsWith(".xlsx")) {
            return extractTextFromExcelXml();
        } else {
            throw new Exception("File type not expected: " + path.getName());
        }
    }

    private String extractTextFromExcelOle() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var workBook = new HSSFWorkbook(new POIFSFileSystem(fis));
                var extractor = new ExcelExtractor(workBook);) {
                extractor.setFormulasNotResults(false);
                extractor.setIncludeSheetNames(true);
                return extractor.getText();
            }
        }
    }

    private String extractTextFromExcelXml() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var workbook = new XSSFWorkbook(fis);
                 var extractor = new XSSFExcelExtractor(workbook)) {
                extractor.setFormulasNotResults(false);
                extractor.setIncludeSheetNames(true);
                return extractor.getText();
            }
        }
    }

    private String extractTextFromPowerPoint() throws Exception {
        if (path.getName().toLowerCase().endsWith(".ppt")) {
            return extractTextFromPowerPointOle();
        } else if (path.getName().toLowerCase().endsWith(".pptx")) {
            return extractTextFromPowerPointXml();
        } else {
            throw new Exception("File type not expected: " + path.getName());
        }
    }

    @SuppressWarnings("all")
    private String extractTextFromPowerPointOle() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var slideShow = SlideShowFactory.create(fis);
                var extractor = new SlideShowExtractor(slideShow);) {
                extractor.setMasterByDefault(true);
                extractor.setSlidesByDefault(true);
                extractor.setNotesByDefault(true);
                extractor.setCommentsByDefault(true);
                return extractor.getText();
            }
        }
    }

    @SuppressWarnings("all")
    private String extractTextFromPowerPointXml() throws Exception {
        try (var fis = new FileInputStream(path)) {
            try (var slideShow = new XMLSlideShow(fis);
                 var extractor = new SlideShowExtractor(slideShow);) {
                extractor.setMasterByDefault(true);
                extractor.setSlidesByDefault(true);
                extractor.setNotesByDefault(true);
                extractor.setCommentsByDefault(true);
                return extractor.getText();
            }
        }
    }

}
