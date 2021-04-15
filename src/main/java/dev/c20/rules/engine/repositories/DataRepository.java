package dev.c20.rules.engine.repositories;

import dev.c20.rules.engine.entities.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DataRepository extends JpaRepository<Data, Long> {

    @Query( "select o from Data o where o.parent = ?1 ")
    public Data getByParent(Long parent);

    @Modifying
    @Query( "insert into Data ( parent, data ) select ?1, data from Data o where o.parent = ?2")
    @Transactional
    public int copyTo(Long target, Long source);

    @Query( "select o.data from Data o, Storage s where o.parent = s.id and s.path = ?1")
    public String getDataOf( String path );



}
