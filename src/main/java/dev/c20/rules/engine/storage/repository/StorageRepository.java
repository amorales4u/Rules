package dev.c20.rules.engine.storage.repository;


import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Query( "select o from Storage o where o.path = ?1 and o.isFolder = false")
    Storage getFile(String path );

    @Query( "select o from Storage o where o.path = ?1 and o.isFolder = true")
    Storage getFolder(String path );

    @Query( "   select o from Storage o " +
            "    where o.path like :#{#path.pathDbLIKE} " +
            "      and ( o.isFolder =  :#{#path.showFolders}  " +
            "            or o.isFolder =  :#{!#path.showFiles} ) " +
            "      and o.level >= :#{#path.level +1} " +
            "      and o.level <= :#{#path.maxLevel +1} " +
            "      and ( :#{#path.image} is null or o.image = :#{#path.image} )" +
            " order by o.path")
    List<Storage> dir(@Param("path") StoragePathUtil path );

    @Transactional
    @Modifying
    @Query( " delete from Storage where path like :#{#path.pathDbLIKE} and level >= :#{#path.level +1} ")
    int deleteChilden( @Param("path") StoragePathUtil path  );

}
