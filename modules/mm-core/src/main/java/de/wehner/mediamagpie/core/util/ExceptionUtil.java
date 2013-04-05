package de.wehner.mediamagpie.core.util;

public class ExceptionUtil {

    /**
     * Converts the give exception to a runtime exception. This also resets the interrupt flag, if
     * the given exception is an {@link InterruptedException}.
     * 
     * @param throwable
     * @return
     */
    public static RuntimeException convertToRuntimeException(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        retainInterruptFlag(throwable);
        return new RuntimeException(throwable);
    }

    /**
     * Sets the interrupt flag if the catched exception was an {@link InterruptedException}, because
     * catching an {@link InterruptedException} clears the interrupt flag.
     * 
     * @param throwable
     *            The catched exception.
     */
    public static void retainInterruptFlag(Throwable throwable) {
        if (throwable instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the root exception.
     * 
     * @param throwable
     * @return
     */
    public static Throwable getRootCause(Throwable throwable) {
        while (throwable.getCause() != null && throwable.getCause() != throwable) {
            throwable = throwable.getCause();
        }

        return throwable;
    }
}
