package de.wehner.mediamagpie.conductor.spring.deploy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Qualifier
public @interface DeployMode {

    public static final String KEY = "deploy.mode";

    public static enum DeployModeType {
        LOCAL, TEST, LIVE, ANY;

        public static DeployModeType lookup(String deployMode) {
            for (DeployModeType deployModeType : values()) {
                if (deployModeType.name().equalsIgnoreCase(deployMode)) {
                    return deployModeType;
                }
                
            }
            return null;
        }
    }

    DeployModeType value();

}
