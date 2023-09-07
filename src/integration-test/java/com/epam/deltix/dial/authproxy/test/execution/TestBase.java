package com.epam.deltix.dial.authproxy.test.execution;

import com.epam.deltix.gflog.api.Log;
import com.epam.deltix.gflog.api.LogFactory;

public class TestBase extends Configuration {
    protected static final Log log = LogFactory.getLog(TestBase.class);

    protected void output(String message) {
        log.info().append(message).commit();
    }
}
