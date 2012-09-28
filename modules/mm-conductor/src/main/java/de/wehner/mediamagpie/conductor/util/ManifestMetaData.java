package de.wehner.mediamagpie.conductor.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.util.ExceptionUtil;

public class ManifestMetaData {

    private static final Logger LOG = LoggerFactory.getLogger(ManifestMetaData.class);

    private static String _version = "unknown";
    private static String _buildTime = "unknown";
    private static boolean _parsed = false;

    public ManifestMetaData() {
        String clazzLocation = ManifestMetaData.class.getProtectionDomain().getCodeSource().getLocation().toString();
        try {
            URL manifestUrl = new URL("jar:" + clazzLocation + "!/META-INF/MANIFEST.MF");
            readManifest(manifestUrl, false);
        } catch (Exception e) {
            LOG.warn("Failed to get version from the manifest file of jar '" + clazzLocation + "' : " + e.getMessage());
        }
    }

    public ManifestMetaData(URL manifestUrl) {
        if (manifestUrl == null) {
            String clazzLocation = ManifestMetaData.class.getProtectionDomain().getCodeSource().getLocation().toString();
            try {
                manifestUrl = new URL("jar:" + clazzLocation + "!/META-INF/MANIFEST.MF");
            } catch (MalformedURLException e) {
                ExceptionUtil.convertToRuntimeException(e);
            }
        }
        readManifest(manifestUrl, true);
    }

    void readManifest(URL manifestUrl, boolean forceReload) {
        if (_parsed && !forceReload) {
            // nothing to do
            return;
        }

        try {
            Manifest manifest = new Manifest(manifestUrl.openStream());
            Attributes mainAttributes = manifest.getMainAttributes();
            _buildTime = mainAttributes.getValue("Build-Time");
            _version = mainAttributes.getValue("Implementation-Version");
        } catch (IOException e) {
            LOG.warn("Failed to get version from the manifest file '" + manifestUrl + "' : " + e.getMessage());
        }
    }

    public String getBuildTime() {
        return _buildTime;
    }

    public String getVersion() {
        return _version;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
