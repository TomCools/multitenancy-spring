package be.tomcools.multitenantspring.dummy;

import be.tomcools.multitenantspring.MultiTenantSpringApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static be.tomcools.multitenantspring.TestConstants.TENANT_1;
import static be.tomcools.multitenantspring.TestConstants.TENANT_2;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MultiTenantSpringApplication.class)
public class DummyIntegrationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    /*
        These tests are mainly just to show the Multi Tenancy
     */

    @Test
    public void whenOneTenantAddsDummy_itCanReadIt() {
        addFor(TENANT_1, DummyEntity.builder().name("name").build());

        final ResponseEntity<DummyEntity[]> tenant = getList(TENANT_1);

        assertThat(tenant.getBody()).hasSize(1);
    }

    @Test
    public void whenOneTenantAddsDummy_otherTenantCantReadIt() {
        addFor(TENANT_1, DummyEntity.builder().name("name").build());

        final ResponseEntity<DummyEntity[]> tenant = getList(TENANT_2);

        assertThat(tenant.getBody()).isEmpty();
    }

    private void addFor(String tenant, DummyEntity entity) {
        HttpHeaders headers = createHeaders(tenant);

        HttpEntity<DummyEntity> entityReq = new HttpEntity<>(entity, headers);
        restTemplate.exchange(createURLWithPort("/dummy"), HttpMethod.POST, entityReq, Object.class, tenant);
    }

    private ResponseEntity<DummyEntity[]> getList(String tenant) {
        HttpHeaders headers = createHeaders(tenant);

        return restTemplate.exchange(createURLWithPort("/dummy"), HttpMethod.GET, new HttpEntity<>(headers), DummyEntity[].class, tenant);
    }

    private HttpHeaders createHeaders(String tenant) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-TENANT", tenant);
        return headers;
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}






