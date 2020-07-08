package be.tomcools.multitenantspring.dummy;

import be.tomcools.multitenantspring.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("dummy")
public class DummyController {

    @Autowired
    private DummyRespository respository;

    @PostMapping
    public void add(@RequestBody DummyEntity entity) {
        respository.save(entity);
    }

    @GetMapping
    public List<DummyEntity> getAll() {
        return respository.findAll();
    }
}
