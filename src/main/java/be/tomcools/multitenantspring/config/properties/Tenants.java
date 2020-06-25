package be.tomcools.multitenantspring.config.properties;

import java.util.Iterator;
import java.util.List;

public class Tenants implements Iterable<TenantData> {
    private final List<TenantData> all;

    public Tenants(List<TenantData> all) {
        this.all = all;
    }

    @Override
    public Iterator<TenantData> iterator() {
        return all.iterator();
    }
}
