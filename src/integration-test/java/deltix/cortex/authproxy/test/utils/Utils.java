package deltix.cortex.authproxy.test.utils;

import deltix.gflog.Log;
import org.junit.Assert;

public class Utils {
    public static void logExceptionAndFail(final Log log, Throwable exception) {
        if (exception instanceof AssertionError) {
            log.error().append("Exception: ").append(exception).commit();
            throw (AssertionError)exception;
        }
        log.error().append("Exception: ").append(exception).commit();
        Assert.fail(exception.getMessage());
    }
}
