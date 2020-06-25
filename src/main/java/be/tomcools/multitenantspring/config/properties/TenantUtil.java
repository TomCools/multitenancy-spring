package be.tomcools.multitenantspring.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class TenantUtil {

    @Value("${tenants.dir}")
    private String tenantsDirectory;

    private Tenants tenants;

    @PostConstruct
    public void init() {
        getAll();
    }

    public synchronized Tenants getAll() {
        if (tenants != null) {
            return tenants;
        }
        List<TenantData> tenantList = new ArrayList<>();
        try {
            final File tenantFolder = ResourceUtils.getFile(tenantsDirectory);
            for (File tenantFile : tenantFolder.listFiles()) {
                final Properties tenantProperties = new Properties();
                tenantProperties.load(new FileInputStream(tenantFile));
                final TenantData tenantData = loadTenantProperties(tenantProperties, tenantFile);
                tenantList.add(tenantData);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("EEK!", e); // TODO: Don't say EEK!
        }

        this.tenants = new Tenants(tenantList);
        return this.tenants;
    }

    private TenantData loadTenantProperties(Properties tenantProperties, File tenantFile) {
        return TenantData.builder()
                .name(tenantProperties.getProperty("name", ""))
                .datasourceUrl(tenantProperties.getProperty("datasource.url", ""))
                .dataSourceUsername(tenantProperties.getProperty("datasource.username", ""))
                .dataSourcePassword(tenantProperties.getProperty("datasource.password", ""))
                .propertiesFileName(tenantFile.getName())
                .build();
    }
}
