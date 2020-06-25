package be.tomcools.multitenantspring.dummy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyRespository extends JpaRepository<DummyEntity, Long> {
}
