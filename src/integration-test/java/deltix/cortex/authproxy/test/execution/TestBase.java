package deltix.cortex.authproxy.test.execution;

import deltix.gflog.Log;
import deltix.gflog.LogFactory;

public class TestBase extends Configuration {
    protected static final Log log = LogFactory.getLog(TestBase.class);

    protected void output(String message) {
        log.info().append(message).commit();
    }
}
