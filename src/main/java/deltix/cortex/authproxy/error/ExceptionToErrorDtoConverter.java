package deltix.cortex.authproxy.error;

import deltix.cortex.authproxy.dto.ErrorDto;
import javax.ws.rs.WebApplicationException;

public class ExceptionToErrorDtoConverter {
    public static ErrorDto getErrorDto(Throwable ex) {
        ErrorDto ret = getErrorDtoOrNull(ex);
        if (ret == null) {
            ret = new ErrorDto();
            ret.setMessage(ex.getMessage());
        }

        return ret;
    }

    private static ErrorDto getErrorDtoOrNull(Throwable ex) {
        if (ex instanceof WebApplicationException) {
            final WebApplicationException wae = (WebApplicationException)ex;
            Object entry = wae.getResponse().getEntity();

            if (entry instanceof ErrorDto)
                return (ErrorDto) entry;
        }
        final Throwable cause = ex.getCause();

        if (cause != null && cause != ex)
            return getErrorDtoOrNull(cause);

        return null;
    }
}