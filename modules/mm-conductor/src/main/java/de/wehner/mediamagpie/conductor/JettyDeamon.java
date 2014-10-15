package de.wehner.mediamagpie.conductor;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public class JettyDeamon implements Daemon {

    private Jetty9Wrapper jetty9Wrapper;

    @Override
    public void init(DaemonContext context) throws DaemonInitException, Exception {
        System.out.println("init: " + context.getArguments());
        jetty9Wrapper = new Jetty9Wrapper();
    }

    @Override
    public void start() throws Exception {
        System.out.println("start " + getClass().getSimpleName());
        jetty9Wrapper.start();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop " + getClass().getSimpleName());
        jetty9Wrapper.stop();
    }

    @Override
    public void destroy() {
        System.out.println("destroy " + getClass().getSimpleName());
    }

}
