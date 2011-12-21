package de.wehner.mediamagpie.common.core.util;

import java.net.URL;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestMetaData {

    private static final Logger LOG = LoggerFactory.getLogger(ManifestMetaData.class);

    private static String _version = "unknown";
    private static String _revision = "unknown";
    private static String _compileTime = "unknown";
    private static long _rawVersion = 1L;

    static {
        String clazzLocation = ManifestMetaData.class.getProtectionDomain().getCodeSource().getLocation().toString();
        try {
            URL manifestUrl = new URL("jar:" + clazzLocation + "!/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(manifestUrl.openStream());
            Map<String, Attributes> entries = manifest.getEntries();
            Attributes dapEntry = entries.get("dap");
            _version = (String) dapEntry.get(new Attributes.Name("Implementation-Version"));
            _revision = (String) dapEntry.get(new Attributes.Name("Git-Revision"));
            _compileTime = (String) dapEntry.get(new Attributes.Name("Compile-Time"));

            // jz: to keep things simple we don't connect version and SERIAL_VERSION_UID since the
            // version is not available in all environments (eclipse, test,...)
            // _rawVersion = Long.parseLong((String) dapEntry.get(new
            // Attributes.Name("Raw-Version")));
        } catch (Exception e) {
            LOG.warn("Failed to get version from the manifest file of jar '" + clazzLocation + "' : " + e.getMessage());
        }
    }

    public final static long SERIAL_VERSION_UID = _rawVersion;

    private ManifestMetaData() {
    }

    public static String getCompileTime() {
        return _compileTime;
    }

    public static String getRevision() {
        return _revision;
    }

    public static String getVersion() {
        return _version;
    }
}
