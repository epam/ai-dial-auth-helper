package com.epam.aidial.auth.helper.utils;

import com.epam.deltix.gflog.core.LogConfigurator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public final class LoggingConfigurator {
    public static void configure(Class<?> clazz, String defaultPath) throws Exception {
        final String configurationFile = System.getProperty("gflog.configuration");
        if (configurationFile != null) {
            LogConfigurator.configure(configurationFile);
            return;
        }

        final String targetPath = System.getProperty("gflog.fileAppender.targetPath");
        if (targetPath == null) {
            System.setProperty("gflog.fileAppender.targetPath", defaultPath);
        }

        final URL resource = clazz.getClassLoader().getResource("config/gflog.xml");
        if (resource == null) {
            System.err.println("Logger configuration is not provided - logging will be disabled.");
            return;
        }

        if (resource.toString().startsWith("jar:")) {
            final File file = File.createTempFile("tempfile", ".tmp");
            try (final InputStream input = resource.openStream(); final FileOutputStream output = new FileOutputStream(file)) {
                int read;
                byte[] buffer = new byte[0x1000];

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
            }

            LogConfigurator.configure(file.toURI().toURL().getFile());
            if (!file.delete()) {
                System.err.println("Cannot delete temporary file: " + file.getAbsolutePath());
            }
        } else {
            LogConfigurator.configure(resource.getFile());
        }
    }

    public static void unconfigure() {
        LogConfigurator.unconfigure();
    }
}
