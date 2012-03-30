package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.IOException;

import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

public abstract class MMAbstractFileSystemProvider extends MMFileSystemProvider {
    
    private static String[] split(String paramString) {
        String[] arrayOfString = new String[2];
        int i = paramString.indexOf(58);
        if (i == -1) {
            arrayOfString[0] = "basic";
            arrayOfString[1] = paramString;
        } else {
            arrayOfString[0] = paramString.substring(0, i++);
            arrayOfString[1] = (i == paramString.length() ? "" : paramString.substring(i));
        }
        return arrayOfString;
    }

//    abstract DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption);
//
//    public final void setAttribute(Path paramPath, String paramString, Object paramObject, LinkOption[] paramArrayOfLinkOption) throws IOException {
//        String[] arrayOfString = split(paramString);
//        if (arrayOfString[0].length() == 0)
//            throw new IllegalArgumentException(paramString);
//        DynamicFileAttributeView localDynamicFileAttributeView = getFileAttributeView(paramPath, arrayOfString[0], paramArrayOfLinkOption);
//        if (localDynamicFileAttributeView == null)
//            throw new UnsupportedOperationException("View '" + arrayOfString[0] + "' not available");
//        localDynamicFileAttributeView.setAttribute(arrayOfString[1], paramObject);
//    }
//
//    public final Map<String, Object> readAttributes(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption) throws IOException {
//        String[] arrayOfString = split(paramString);
//        if (arrayOfString[0].length() == 0)
//            throw new IllegalArgumentException(paramString);
//        DynamicFileAttributeView localDynamicFileAttributeView = getFileAttributeView(paramPath, arrayOfString[0], paramArrayOfLinkOption);
//        if (localDynamicFileAttributeView == null)
//            throw new UnsupportedOperationException("View '" + arrayOfString[0] + "' not available");
//        return localDynamicFileAttributeView.readAttributes(arrayOfString[1].split(","));
//    }

    abstract boolean implDelete(MMPath paramPath, boolean paramBoolean) throws IOException;

    public final void delete(MMPath paramPath) throws IOException {
        implDelete(paramPath, true);
    }

    public final boolean deleteIfExists(MMPath paramPath) throws IOException {
        return implDelete(paramPath, false);
    }
}
