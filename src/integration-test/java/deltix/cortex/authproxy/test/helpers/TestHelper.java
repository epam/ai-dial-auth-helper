package deltix.cortex.authproxy.test.helpers;

public class TestHelper {
    private TestHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String buildAddress(String protocol, String host, int port, String url) {
        if (host == null)
            throw new IllegalArgumentException("host == null");
        if (port < 0)
            return String.format("%s://%s/%s", protocol, host, url);
        return String.format("%s://%s:%d/%s", protocol, host, port, url);
    }
}
