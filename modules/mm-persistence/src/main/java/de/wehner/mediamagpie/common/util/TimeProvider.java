package de.wehner.mediamagpie.common.util;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class TimeProvider implements Serializable {

    private static final long serialVersionUID = -1;

    public long getTime() {
        return System.currentTimeMillis();
    }
}
