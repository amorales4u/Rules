package dev.c20.rules.engine.storage.repository;

import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.entities.adds.Data;
import dev.c20.rules.engine.storage.entities.adds.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    @Query( "select o from Word o where o.parent = ?1 ")
    public Data getByParent(Long parent);

    @Query( "select o.data from Data o, Storage s where o.parent = s.id and s.path = ?1")
    public String getDataOf( String path );

    @Modifying
    @Query( "delete from Word where parent = ?1")
    @Transactional
    public int deleteAll(Storage target);



}
