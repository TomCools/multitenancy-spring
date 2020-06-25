package be.tomcools.multitenantspring.datasource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class MultiTenantLiquibaseConfiguration implements ApplicationContextAware {
    @Value("${liquibase.change-log}")
    private String liquibaseChangeLog;

    @Value("${liquibase.contexts}")
    private String liquibaseContext;

    @Value("${liquibase.drop-first}")
    private boolean liquibaseDropFirst;

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    public void registerTenant(final DataSource ds, final String tenantId, final Map<String, String> parameters) {
        final BeanDefinition liquibaseDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(SpringLiquibase.class)
                .addPropertyValue("dataSource", ds)
                .addPropertyValue("changeLog", this.liquibaseChangeLog)
                .addPropertyValue("dropFirst", this.liquibaseDropFirst)
                .addPropertyValue("changeLogParameters", parameters)
                .addPropertyValue("contexts", this.liquibaseContext)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();

        ((BeanDefinitionRegistry) this.beanFactory).registerBeanDefinition(tenantId + "LiquibaseBean", liquibaseDefinition);
    }
}
