package de.wehner.mediamagpie.conductor.webapp.util;

public class Env {

    public static final String SERVER_ADDRESS_KEY = "server.address";
    public static final String DEPLOY_MODE_KEY = "deploy.mode";

    private Env() {
        // prohibit execution
    }

    public static String getDeployMode() {
        return System.getProperty(DEPLOY_MODE_KEY);
    }

    public static String getServerAddress() {
        return System.getProperty(SERVER_ADDRESS_KEY);
    }

    public static String getServerUrl() {
        return "http://" + getServerAddress();
    }

}
