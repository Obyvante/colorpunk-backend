package com.barden.bravo.http;

import com.barden.bravo.settings.Settings;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTP validation class.
 */
@Component
public final class HTTPValidation implements Filter {

    @Override
    public void doFilter(@Nonnull ServletRequest request,
                         @Nonnull ServletResponse response,
                         @Nonnull FilterChain chain) throws IOException, ServletException {
        //Gets HTTP request.
        HttpServletRequest http_request = (HttpServletRequest) request;
        HttpServletResponse http_response = (HttpServletResponse) response;
        String http_api_key = ((HttpServletRequest) request).getHeader("BARDEN-API-KEY");

        //If HTTP API key is not valid, no need to continue.
        if (http_api_key == null || !this.isValidKey(http_api_key)) {
            //Configures response.
            http_response.setStatus(401);
            http_response.setContentType("application/json");
            http_response.setCharacterEncoding("UTF-8");
            http_response.getWriter().write(HTTPRepository.createResponse(false, Result.INVALID_API_KEY).toString());
            return;
        }

        //Continues filtering.
        chain.doFilter(request, response);
    }

    /**
     * Results.
     */
    public enum Result {
        INVALID_API_KEY
    }

    /**
     * Gets if declared API key is valid or not.
     *
     * @param key API key.
     * @return if declared API KEY is valid or not.
     */
    private boolean isValidKey(@Nonnull String key) {
        return key.equals(Settings.getKey());
    }
}
