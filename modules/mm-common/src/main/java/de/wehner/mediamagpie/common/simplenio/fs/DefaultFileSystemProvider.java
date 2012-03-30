package de.wehner.mediamagpie.common.simplenio.fs;

import java.security.AccessController;

import sun.security.action.GetPropertyAction;
import de.wehner.mediamagpie.common.simplenio.file.spi.MMFileSystemProvider;

public class DefaultFileSystemProvider {
    
    private static MMFileSystemProvider createProvider(String paramString) {
//        return (MMFileSystemProvider) AccessController.doPrivileged(new PrivilegedAction<MMFileSystemProvider>(paramString) {
//            public MMFileSystemProvider run() {
//                Class localClass;
//                try {
//                    localClass = Class.forName(this.val$cn, true, null);
//                } catch (ClassNotFoundException localClassNotFoundException) {
//                    throw new AssertionError(localClassNotFoundException);
//                }
//                try {
//                    return (MMFileSystemProvider) localClass.newInstance();
//                } catch (IllegalAccessException localIllegalAccessException) {
//                    throw new AssertionError(localIllegalAccessException);
//                } catch (InstantiationException localInstantiationException) {
//                }
//                throw new AssertionError(localInstantiationException);
//            }
//        });
        
        // TODO rwe: build clear solution later
        return new MMUnixFileSystemProvider();
    }

    public static MMFileSystemProvider create() {
        String str = (String) AccessController.doPrivileged(new GetPropertyAction("os.name"));

        if (str.equals("SunOS"))
            return createProvider("sun.nio.fs.SolarisFileSystemProvider");
        if (str.equals("Linux"))
            return createProvider("sun.nio.fs.LinuxFileSystemProvider");
        if ((str.equals("Darwin")) || (str.startsWith("Mac OS X")))
            return createProvider("sun.nio.fs.BsdFileSystemProvider");
        throw new AssertionError("Platform not recognized");
    }
}