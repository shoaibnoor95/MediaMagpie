package de.wehner.mediamagpie.common.util;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileSystemUtil {

    public final static String FILE_COUNTER_SEPARATOR = "-mmcounter-";

    public static File getNextUniqueFilename(File existingFile) {
        if (!existingFile.exists()) {
            throw new IllegalArgumentException("The argument 'existingFile' is not an existing file.");
        }

        String baseFileName = FilenameUtils.getBaseName(existingFile.getName());
        String[] nameAndCounter = StringUtils.splitByWholeSeparator(baseFileName, FILE_COUNTER_SEPARATOR);
        String extension = FilenameUtils.getExtension(existingFile.getName());
        if (!StringUtils.isEmpty(extension)) {
            extension = '.' + extension;
        }
        File testFile;
        if (nameAndCounter.length == 1) {
            testFile = new File(existingFile.getParentFile(), String.format("%s%s1%s", baseFileName, FILE_COUNTER_SEPARATOR, extension));
        } else {
            int actualCounter = Integer.parseInt(nameAndCounter[1]);
            testFile = new File(existingFile.getParentFile(), String.format("%s%s%d%s", nameAndCounter[0], FILE_COUNTER_SEPARATOR, (actualCounter + 1),
                    extension));
        }
        if (testFile.exists()) {
            return getNextUniqueFilename(testFile);
        }
        return testFile;
    }
}
