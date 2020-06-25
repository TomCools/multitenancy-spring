package be.tomcools.multitenantspring.dummy;

import be.tomcools.multitenantspring.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dummy")
public class DummyController {

    @Autowired
    private DummyRespository respository;

    @GetMapping("/add")
    public void add(@RequestParam String tenant, @RequestParam String name) {
        // TODO -> Put this in a filter!
        TenantContext.setupNewContext(tenant);
        respository.save(DummyEntity.builder().name(name).build());
    }

    @GetMapping
    public List<DummyEntity> add(@RequestParam String tenant) {
        TenantContext.setupNewContext(tenant);
        return respository.findAll();
    }
}
