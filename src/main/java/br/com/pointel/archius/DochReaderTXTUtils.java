package br.com.pointel.archius;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import br.com.pointel.jarch.mage.WizArray;

public class DochReaderTXTUtils {

    public static String[] TXT_EXTENSIONS = new String[]{"txt", "md", "htm", "html", "php", "phtml", "java", "py", "log"};

    public static boolean isTXTFile(File file) {
        return isTXTFile(file.getName());
    }

    public static boolean isTXTFile(String fileName) {
        return WizArray.has(FilenameUtils.getExtension(fileName).toLowerCase(), TXT_EXTENSIONS);
    }
    
}
