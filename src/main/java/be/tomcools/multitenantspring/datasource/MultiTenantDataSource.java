package be.tomcools.multitenantspring.datasource;

import be.tomcools.multitenantspring.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class MultiTenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        if (TenantContext.hasContext()) {
            return TenantContext.getCurrentTenant();
        } else {
            String threadName = Thread.currentThread().getName();
            log.warn("[" + threadName + "] No ThreadContext present, so tenant is null => fallback to default tenant!");
            return null;
        }
    }
}
