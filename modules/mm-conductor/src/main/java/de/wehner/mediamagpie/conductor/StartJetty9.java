package de.wehner.mediamagpie.conductor;

import org.eclipse.jetty.server.Server;

public class StartJetty9 {

    public static void main(String[] args) throws Exception {
        Jetty9Wrapper jetty9 = new Jetty9Wrapper();
        Server server = jetty9.start();
        server.join();
    }

}
