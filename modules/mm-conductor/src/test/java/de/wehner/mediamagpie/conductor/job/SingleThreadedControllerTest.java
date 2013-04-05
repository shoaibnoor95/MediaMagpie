package de.wehner.mediamagpie.conductor.job;

import static org.junit.Assert.*;

import org.junit.Test;

public class SingleThreadedControllerTest {

    @Test
    public void testInterruptablity() throws InterruptedException {
        final SingleThreadedController controller = createController();

        Thread thread = new Thread() {
            @Override
            public void run() {
                controller.loop();
            }
        };

        thread.start();
        thread.interrupt();
        thread.join(1000);

        // interrupting the thread should terminate the controller
        assertFalse(thread.isAlive());
    }

    @Test
    public void testStopWithoutStart() throws InterruptedException {
        SingleThreadedController controller = createController();

        controller.stop();
    }

    private SingleThreadedController createController() {
        SingleThreadedController controller = new SingleThreadedController() {

            @Override
            protected boolean execute() {
                return false;
            }

        };
        return controller;
    }
}
