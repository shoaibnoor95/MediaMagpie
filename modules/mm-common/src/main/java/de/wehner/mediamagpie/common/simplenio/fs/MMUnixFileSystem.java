package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.IOException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sun.security.action.GetPropertyAction;

import de.wehner.mediamagpie.common.simplenio.file.MMFileSystem;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

public class MMUnixFileSystem extends MMFileSystem {

    public final MMPath getPath(String paramString, String... paramArrayOfString) {
        String str1;
        if (paramArrayOfString.length == 0) {
            str1 = paramString;
        } else {
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(paramString);
            for (String str2 : paramArrayOfString) {
                if (str2.length() > 0) {
                    if (localStringBuilder.length() > 0)
                        localStringBuilder.append('/');
                    localStringBuilder.append(str2);
                }
            }
            str1 = localStringBuilder.toString();
        }
        return new MMUnixPath(this, str1);
    }

    private final MMUnixFileSystemProvider provider;
    private final byte[] defaultDirectory;
    private final boolean needToResolveAgainstDefaultDirectory;
    private final MMUnixPath rootDirectory;
    private static final String GLOB_SYNTAX = "glob";
    private static final String REGEX_SYNTAX = "regex";

    MMUnixFileSystem(MMUnixFileSystemProvider paramUnixFileSystemProvider, String paramString) {
        this.provider = paramUnixFileSystemProvider;
        this.defaultDirectory = MMUnixPath.normalizeAndCheck(paramString).getBytes();
        if (this.defaultDirectory[0] != 47) {
            throw new RuntimeException("default directory must be absolute");
        }

        String str = (String) AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.chdirAllowed", "false"));

        boolean bool = str.length() == 0 ? true : Boolean.valueOf(str).booleanValue();

        if (bool) {
           this.needToResolveAgainstDefaultDirectory = true;
        } else {
            // TODO rwe: ???
            ;
//            byte[] arrayOfByte = UnixNativeDispatcher.getcwd();
//            int i = arrayOfByte.length == this.defaultDirectory.length ? 1 : 0;
//            if (i != 0) {
//                for (int j = 0; j < arrayOfByte.length; j++) {
//                    if (arrayOfByte[j] != this.defaultDirectory[j]) {
//                        i = 0;
//                        break;
//                    }
//                }
//            }
//            this.needToResolveAgainstDefaultDirectory = (i == 0);
          this.needToResolveAgainstDefaultDirectory = false;//(i == 0);
        }

        this.rootDirectory = new MMUnixPath(this, "/");
    }

    byte[] defaultDirectory() {
        return this.defaultDirectory;
    }

    boolean needToResolveAgainstDefaultDirectory() {
        return this.needToResolveAgainstDefaultDirectory;
    }

    MMUnixPath rootDirectory() {
        return this.rootDirectory;
    }

    boolean isSolaris() {
        return false;
    }

    static List<String> standardFileAttributeViews() {
        return Arrays.asList(new String[] { "basic", "posix", "unix", "owner" });
    }

    public final MMFileSystemProvider provider() {
        return this.provider;
    }

    public final String getSeparator() {
        return "/";
    }

    public final boolean isOpen() {
        return true;
    }

    public final boolean isReadOnly() {
        return false;
    }

    public final void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    void copyNonPosixAttributes(int paramInt1, int paramInt2) {
    }

}
