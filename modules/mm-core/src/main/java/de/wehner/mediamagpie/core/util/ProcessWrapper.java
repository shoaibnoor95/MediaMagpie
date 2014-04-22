package de.wehner.mediamagpie.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessWrapper.class);

    public static abstract class StdXXXLineListener {

        /**
         * Callback for new received line from stdout
         * 
         * @param line
         *            the line from stdout
         * @return <code>true</code> if more lines will be received, <code>false</code> if no more lines will be read any more from process
         */
        public abstract boolean fireNewLine(String line);

    }

    private final ProcessBuilder _processBuilder;
    private Process _process;
    private Thread stdOutListenerThread;

    public ProcessWrapper(ProcessBuilder processBuilder) {
        super();
        _processBuilder = processBuilder;
    }

    /**
     * Start the wrapped process now.
     * 
     * @param newStdOutLineListener
     *            optional a listener to stdout and stderr
     * @throws IOException
     *             if program can not be started. (Eg: the program does not exist on file system or location)
     */
    public void start(final StdXXXLineListener newStdOutLineListener) throws IOException {
        _processBuilder.redirectErrorStream(true);
        _process = _processBuilder.start();

        if (newStdOutLineListener != null) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(_process.getInputStream()));
                    try {
                        String line;
                        try {
                            while ((line = stdOutReader.readLine()) != null) {
                                boolean needMoreLines = newStdOutLineListener.fireNewLine(line);
                                if (!needMoreLines) {
                                    break;
                                }
                            }
                            LOG.info("stdout stream is dead so termitate stdout listener thread clearly.");
                        } catch (IOException e) {
                        }
                    } finally {
                        IOUtils.closeQuietly(stdOutReader);
                    }
                }
            };

            stdOutListenerThread = new Thread(runnable);
            stdOutListenerThread.start();
        }
    }

    public boolean isRunning() {
        if (_process == null) {
            return false;
        }
        try {
            _process.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public int getExitValue() {
        return _process.exitValue();
    }

    public void destroy() {
        // send a normal kill to process
        int pid = getUnixPID(_process);
        executeCommand("kill", "" + pid);
        new TimeoutExecutor(5000, 10).checkUntilConditionIsTrue(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean running = isRunning();
                return !running;
            }
        });
        if (isRunning()) {
            // send kill -9 if process is not dead
            executeCommand("kill", "-9", "" + pid);
            new TimeoutExecutor(5000, 10).checkUntilConditionIsTrue(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return !isRunning();
                }
            });
        }

        // destroy the java process object
        _process.destroy();

        if (stdOutListenerThread != null) {
            stdOutListenerThread.interrupt();
        }
    }

    private void executeCommand(String... cmd) {
        ProcessBuilder pBuilder = new ProcessBuilder(cmd);
        try {
            LOG.debug("start command '" + Arrays.asList(cmd) + "'");
            Process myProcess = pBuilder.start();
            IOUtils.closeQuietly(myProcess.getOutputStream());
            IOUtils.closeQuietly(myProcess.getInputStream());
            IOUtils.closeQuietly(myProcess.getErrorStream());
        } catch (IOException e) {
            LOG.warn("Can not execute " + Arrays.asList(cmd), e);
            return;
        }
    }

    private Integer getUnixPID(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Class<?> cl = process.getClass();
                Field field = cl.getDeclaredField("pid");
                field.setAccessible(true);
                Object pidObject = field.get(process);
                return (Integer) pidObject;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Needs to be a UNIXProcess");
        }
    }

    /**
     * Causes the current thread to wait, if necessary, until the process represented by this {@code Process} object has terminated. This
     * method returns immediately if the subprocess has already terminated. If the subprocess has not yet terminated, the calling thread
     * will be blocked until the subprocess exits.
     * 
     * @return the exit value of the subprocess represented by this {@code Process} object. By convention, the value {@code 0} indicates
     *         normal termination.
     * @throws InterruptedException
     *             if the current thread is {@linkplain Thread#interrupt() interrupted} by another thread while it is waiting, then the wait
     *             is ended and an {@link InterruptedException} is thrown.
     */
    public int waitFor() throws InterruptedException {
        return _process.waitFor();
    }
}
