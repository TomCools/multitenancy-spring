# Multi-tenancy in Spring with JPA and Liquibase

Includes:

- TenantContext setup
- H2 Databases per tenant
- Liquibase for each H2 on startup
- Simple "dummy" controller and repository
- A simple "security" filter to setup the TenantContext based on Headers

## TenantContext

The TenantContext will be initialized on each request and will contain the name of the tenant.
In this example, we kept it to a simple "Read from Header" setup but 
it does show how you could use it in a more complex situation. Ex. Using JWT token and getting the tenant from that token.

The TenantContext uses a ThreadLocal variable. As a consequence, 
any code that spawns other threads might not have the Tenant value available unless it is explicitly copied.

## Database Setup (H2/Liquibase)

Spring Boot is great, but the config doesn't really support Multi-Tenancy out of the box.
Most of the auto-configuration of Spring Boot will not work and the datasources have to be created manually.

First off, each tenant has it's specific configuration in a separate file (see /resources/tenants).
All files in that folder are read and put into Properties.class which is used to configure the application.

In this example, I chose for H2 databases. They are embedded databases and thus will not require you to spin up a local database to run this example.
This project uses a *datasource<->tenant* approach. 

To enable easy creation of new datasources for new tenants, Liquibase is configured on each datasource.
This will ensure all databases, even of a tenant added much later, will be updated to the latest version of the database-model. 


