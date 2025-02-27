package br.com.pointel.archius;

import java.io.File;
import br.com.pointel.jarch.mage.WizChars;

public class ArchNamer {

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

}
