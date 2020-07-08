package be.tomcools.multitenantspring.security;

import be.tomcools.multitenantspring.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter implements Filter {
    public final static String TENANT_HEADER = "X-TENANT";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        log.info("Starting a transaction for req : {}", req.getRequestURI());

        final String tenant = req.getHeader(TENANT_HEADER);
        if(tenant != null) {
            checkIfTenantExists(tenant);
            TenantContext.setupNewContext(tenant);
        } else {
            // Throw an error or something :-)
        }

        chain.doFilter(request, response);

        TenantContext.clearContext();
    }

    private void checkIfTenantExists(String header) {
        // Could be implemented, not in this setup 'yet';
        // If tenant can not be setup, this filter should block it, as otherwize the Datasource can not be found.
    }
}
