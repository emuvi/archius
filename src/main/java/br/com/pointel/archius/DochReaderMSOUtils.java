package br.com.pointel.archius;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import br.com.pointel.jarch.mage.WizArray;

public class DochReaderMSOUtils {

    public static String[] MSPOWERPOINT_EXTENSIONS = new String[]{"ppt", "pptx"};
    public static String[] MSEXCEL_EXTENSIONS = new String[]{"xls", "xlsx"};
    public static String[] MSWORD_EXTENSIONS = new String[]{"doc", "docx"};

    public static boolean isMSWordFile(File file) {
        return isMSWordFile(file.getName());
    }

    public static boolean isMSWordFile(String fileName) {
        return WizArray.has(FilenameUtils.getExtension(fileName).toLowerCase(), MSWORD_EXTENSIONS);
    }

    public static boolean isMSPowerPointFile(File file) {
        return isMSPowerPointFile(file.getName());
    }

    public static boolean isMSPowerPointFile(String fileName) {
        return WizArray.has(FilenameUtils.getExtension(fileName).toLowerCase(), MSPOWERPOINT_EXTENSIONS);
    }

    public static boolean isMSExcelFile(File file) {
        return isMSExcelFile(file.getName());
    }

    public static boolean isMSExcelFile(String fileName) {
        return WizArray.has(FilenameUtils.getExtension(fileName).toLowerCase(), MSEXCEL_EXTENSIONS);
    }
    
}
