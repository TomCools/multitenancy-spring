package be.tomcools.multitenantspring.config.properties;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TenantData {
    private final String name;
    private final String propertiesFileName;
    private final String datasourceUrl;
    private final String dataSourceUsername;
    private final String dataSourcePassword;
}
