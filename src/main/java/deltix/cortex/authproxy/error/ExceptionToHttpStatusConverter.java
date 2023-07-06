package deltix.cortex.authproxy.error;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import javax.ws.rs.WebApplicationException;

public class ExceptionToHttpStatusConverter {
    public static HttpStatus getStatus(Throwable ex) {
        final HttpStatus ret = getStatusOrNull(ex);

        if (ret == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;

        return ret;
    }

    private static HttpStatus getStatusOrNull(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            final WebApplicationException wae = (WebApplicationException)ex;
            return HttpStatus.valueOf(wae.getResponse().getStatus());
        }

        if (ex instanceof ConversionFailedException
                || ex instanceof IllegalArgumentException
                || ex instanceof IllegalStateException
                || ex instanceof MissingServletRequestParameterException)
            return HttpStatus.BAD_REQUEST;
        final Throwable cause = ex.getCause();

        if (cause != null && cause != ex)
            return getStatusOrNull(cause);

        return null;
    }
}