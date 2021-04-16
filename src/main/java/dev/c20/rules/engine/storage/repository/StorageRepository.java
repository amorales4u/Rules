package dev.c20.rules.engine.storage.repository;


import dev.c20.rules.engine.storage.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Query( "select o from Storage o where o.path = ?1 and o.isFolder = false")
    public Storage getFile(String path );

    @Query( "select o from Storage o where o.path = ?1 and o.isFolder = true")
    public Storage getFolder(String path );

    @Query( "select o from Storage o where o.path like ?1 and o.level = ?2 order by o.isFolder, o.name")
    public List<Storage> dir(String path, Integer level );

}
