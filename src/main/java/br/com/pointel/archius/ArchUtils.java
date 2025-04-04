package br.com.pointel.archius;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import br.com.pointel.jarch.mage.WizChars;

public class ArchUtils {

    public static Boolean isArchFile(File path) {
        return path.getName().startsWith("arch-") 
                        && (path.getName().endsWith(".db3") || path.getName().endsWith(".ser"));
    }

    public static String getFinalName(File folder, String fullNamer) {
        var result = fullNamer;
        var startSlash = result.indexOf('/');
        while (startSlash > 0) {
            var endSlash = result.indexOf('/', startSlash + 1);
            var nextSlash = endSlash; 
            if (endSlash > startSlash) {
                var length = result.substring(startSlash + 1, endSlash).length();
                var prefix = result.substring(0, startSlash);
                var suffix = result.substring(endSlash + 1);
                Integer serial = 0;
                Boolean found = false;
                do {
                    serial++;
                    found = false;
                    var newPrefix = prefix + WizChars.fillAtStart(serial + "", '0', length);
                    for (var file : folder.listFiles()) {
                        if (file.isFile() && file.getName().startsWith(newPrefix)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        result = newPrefix + suffix;
                        nextSlash = newPrefix.length();
                    }
                } while (found);
            }
            startSlash = result.indexOf('/', nextSlash + 1);
        }
        return result;
    }

    public static Map<String, String> getNameParts(List<ConfigNamer> configNamers, String finalName) {
        var result = new HashMap<String, String>();
        var processing = finalName;
        
        for (int i = 0; i < configNamers.size(); i++) {
            var configNamer = configNamers.get(i);
            if (configNamer instanceof ConfigNamerCombo namerCombo) {
                if (namerCombo.getPrefix() != null && !namerCombo.getPrefix().isEmpty()) {
                    if (processing.startsWith(namerCombo.getPrefix())) {
                        processing = processing.substring(namerCombo.getPrefix().length());
                    }
                }
                for (var option : namerCombo.getOptions()) {
                    if (processing.startsWith(option)) {
                        result.put(configNamer.getName(), option);
                        processing = processing.substring(option.length());
                        break;
                    }
                }
                if (namerCombo.getSuffix() != null && !namerCombo.getSuffix().isEmpty()) {
                    if (processing.startsWith(namerCombo.getSuffix())) {
                        processing = processing.substring(namerCombo.getSuffix().length());
                    }
                }
            } else if (configNamer instanceof ConfigNamerField namerField) {
                // |TODO| use NextDelimiter to implement ConfigNamerField
            } else if (configNamer instanceof ConfigNamerSeparator namerSeparator) {
                if (processing.startsWith(namerSeparator.getChars())) {
                    processing = processing.substring(namerSeparator.getChars().length());
                }
            } else if (configNamer instanceof ConfigNamerSerial namerSerial) {
                if (namerSerial.getPrefix() != null && !namerSerial.getPrefix().isEmpty()) {
                    if (processing.startsWith(namerSerial.getPrefix())) {
                        processing = processing.substring(namerSerial.getPrefix().length());
                    }
                }
                while (processing.length() > 0 && Character.isDigit(processing.charAt(0))) {
                    processing = processing.substring(1);
                }
                if (namerSerial.getSuffix() != null && !namerSerial.getSuffix().isEmpty()) {
                    if (processing.startsWith(namerSerial.getSuffix())) {
                        processing = processing.substring(namerSerial.getSuffix().length());
                    }
                }
            }
        }
        return result;
    }

    private static record NextDelimiter(Integer index, String delimiter) {}

}
