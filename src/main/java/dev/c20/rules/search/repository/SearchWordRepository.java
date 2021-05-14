package dev.c20.rules.search.repository;

import dev.c20.rules.storage.tools.FindedStorage;
import dev.c20.rules.search.entities.GlobalWord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchWordRepository extends JpaRepository<GlobalWord, String> {

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
    public List<Long> searchOne( String key );

    @Query( "select o.word from GlobalWord o where o.word like ?1")
    public List<String> searchLike( String word );

    @Query( "select distinct " +
            " new dev.c20.rules.storage.tools.FindedStorage(s.path,s.name,s.description) " +
            "  from Storage s " +
            " where s.id in ( ?1 ) "
    )
    public List<FindedStorage> search(List<Long> ids);

    @Query( "select distinct" +
            " s.id " +
            "  from Storage s, " +
            "       Word w " +
            " where w.parent = s and "+
            "       s.path like ?1 and " +
            "       w.word in ( ?2 ) " +
            " group by s "
    )
    public List<Long> searchIds(String fromPath, List<String> words,  Pageable pageable);



}
