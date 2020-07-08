package be.tomcools.multitenantspring.datasource;

import be.tomcools.multitenantspring.config.properties.TenantData;
import be.tomcools.multitenantspring.config.properties.TenantUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class MultiTenantDataSourceConfiguration {

    private static final int TIMEOUT_FOR_VALIDATION_CONNECTION = 1000;

    @Autowired
    TenantUtil tenantUtil;

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Autowired
    MultiTenantLiquibaseConfiguration liquibaseConfiguration;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(createMultiTenantDataSource());
        em.setPackagesToScan("be.tomcools");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        return em;
    }

    @Bean
    @Primary
    public DataSource createMultiTenantDataSource() {

        Map<Object, Object> dataSourceMap = createDatasourcesForTenants();

        // Setup the multitenant datasource
        final MultiTenantDataSource multiTenantDataSource = new MultiTenantDataSource();
        multiTenantDataSource.setTargetDataSources(dataSourceMap);
        multiTenantDataSource.setDefaultTargetDataSource(getDefaultDatasource());

        // Call this to finalize the initialization of the data source.
        multiTenantDataSource.afterPropertiesSet();

        return multiTenantDataSource;
    }

    private Map<Object, Object> createDatasourcesForTenants() {
        Map<Object, Object> dataSourceMap = new HashMap<>();

        for (TenantData tenantData : tenantUtil.getAll()) {
            try {
                final DataSource dataSource = setupDatasource(tenantData);
                dataSourceMap.put(tenantData.getName(), dataSource);
            } catch (Exception throwables) {
                log.error("Error starting Datasource for tenant: {}", tenantData.getName(), throwables);
            }
        }
        return dataSourceMap;
    }

    private DataSource setupDatasource(TenantData tenantData) throws SQLException {
        // Create a regular Data Source and validation timeout.
        final DataSource dataSource = createDataSource(tenantData.getDatasourceUrl(), tenantData.getDataSourceUsername(), tenantData.getDataSourcePassword());
        dataSource.getConnection().isValid(TIMEOUT_FOR_VALIDATION_CONNECTION);

        // Add a liquibase config for each tenant.
        liquibaseConfiguration.registerTenant(dataSource,tenantData.getName(),new HashMap<>());
        return dataSource;
    }

    private Object getDefaultDatasource() {
        // Create default datasource properties. This needs to exists
        return createDataSource(this.dataSourceProperties.getUrl(), this.dataSourceProperties.getUsername(), this.dataSourceProperties.getPassword());
    }

    private DataSource createDataSource(String url, String username, String password) {
        // Initialize the datasource
        final HikariDataSource hds = dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .username(username)
                .url(url)
                .password(password)
                .build();

        // Not required, but handy in the long run to see different pool names
        hds.setPoolName("HikariPool-"+url);

        // need in Spring boot 2
        hds.setJdbcUrl(url);

        // Optionally, set extra properties on Hikari;
        hds.setConnectionTestQuery("SELECT 1");
        return hds;
    }


}
