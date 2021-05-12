package dev.c20.rules.engine.storage.repository;

import dev.c20.rules.engine.storage.entities.GlobalWord;
import dev.c20.rules.engine.storage.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GlobalWordRepository  extends JpaRepository<GlobalWord, String> {

    @Query( "select o from GlobalWord o where o.word = ?1")
    public GlobalWord get( String word );

    @Query(
    nativeQuery = true,
    value= "select distinct asG.c20_id " +
            "from ( " +
            "         select asW.c20_id, asW.c20_word, count(1) asCount " +
            "         from c20_stg_word asW " +
            "         where asW.c20_word = ( " +
            "             select asgW.c20_word " +
            "             from c20_global_word asGW " +
            "             where asGW.c20_word like '%system%' " +
            "         ) " +
            "         group by asW.c20_id, asW.c20_word " +
            "     ) asG " +
            "order by asG.asCount "
    )
    public List<Long> search( String key );

}
