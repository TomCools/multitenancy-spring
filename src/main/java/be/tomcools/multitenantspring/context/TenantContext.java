package be.tomcools.multitenantspring.context;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
public class TenantContext {
    private static final ThreadLocal<TenantContext> THREAD_LOCAL_TENANT_CONTEXT = new InheritableThreadLocal<>();

    private String tenant;

    public static TenantContext setupNewContext(final String tenant) {
        final TenantContext tc = clearContext();
        tc.setTenant(tenant);
        return tc;
    }

    public static boolean hasContext() {
        return THREAD_LOCAL_TENANT_CONTEXT.get() != null;
    }

    public static String getCurrentTenant() {
        return getThreadLocalTenantContext().getTenant();
    }

    private static TenantContext getThreadLocalTenantContext() {
        final TenantContext tc = THREAD_LOCAL_TENANT_CONTEXT.get();
        if (tc == null) {
            throw new IllegalStateException("There currently is no Thread Context... this is not suppose to happen...");
        }
        return tc;
    }

    public static TenantContext clearContext() {
        THREAD_LOCAL_TENANT_CONTEXT.set(new TenantContext());
        return getThreadLocalTenantContext();
    }
}
