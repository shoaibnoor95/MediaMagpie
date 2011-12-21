package de.wehner.mediamagpie.common.util;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.core.util.ManifestMetaData;


@Component
public class TimeProvider implements Serializable {

    private static final long serialVersionUID = ManifestMetaData.SERIAL_VERSION_UID;

    public long getTime() {
        return System.currentTimeMillis();
    }
}
