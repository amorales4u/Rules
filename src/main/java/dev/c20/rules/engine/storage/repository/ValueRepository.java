package dev.c20.rules.engine.storage.repository;

import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.entities.adds.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ValueRepository extends JpaRepository<Value, Long> {

    @Query( "select o from Value o where o.parent = ?1 order by o.id")
    public List<Value> getAll(Storage parent);

    @Query( "select o from Value o where o.parent = ?1 and o.name = ?2 order by o.id")
    public Value getByName(Storage parent, String name);

    @Modifying
    @Query( "insert into Value ( parent, name, value ) select ?1,name, value from Value o where o.parent = ?2")
    @Transactional
    public int copyTo(Storage target, Storage source);

    @Modifying
    @Query( "delete from Value where parent = ?1")
    @Transactional
    public int deleteAll(Storage target);


}
