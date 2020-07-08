package be.tomcools.multitenantspring.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        if (tenants == null) {
            this.tenants = readTenants();
        }
        return this.tenants;
    }

    private Tenants readTenants() {
        List<TenantData> tenantList = new ArrayList<>();
        try {
            for (File tenantFile : openTenantDirectory()) {
                final Properties tenantProperties = new Properties();
                tenantProperties.load(new FileInputStream(tenantFile));
                final TenantData tenantData = loadTenantProperties(tenantProperties, tenantFile);
                tenantList.add(tenantData);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read Tenant Properties", e); // TODO: Don't say EEK!
        }

        return new Tenants(tenantList);
    }

    private File[] openTenantDirectory() throws FileNotFoundException {
        final File file = ResourceUtils.getFile(tenantsDirectory);
        if(!file.isDirectory()) {
            throw new IllegalArgumentException(String.format("Configured path for Tenant Properties is not a directory: %s", tenantsDirectory));
        }
        final File[] files = file.listFiles();
        if(files == null) {
            throw new IllegalArgumentException(String.format("Could not read files in directory: %s", tenantsDirectory));
        }

        return files;
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
