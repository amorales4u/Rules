package dev.c20.rules.search.services;

import dev.c20.rules.search.requestresponses.SearchRequest;
import dev.c20.rules.search.entities.GlobalWord;
import dev.c20.rules.storage.entities.Storage;
import dev.c20.rules.storage.entities.adds.Data;
import dev.c20.rules.storage.entities.adds.Word;
import dev.c20.rules.search.repository.SearchWordRepository;
import dev.c20.rules.storage.repository.DataRepository;
import dev.c20.rules.storage.repository.StorageRepository;
import dev.c20.rules.storage.repository.WordRepository;
import dev.c20.rules.storage.tools.FindedStorage;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import dev.c20.workflow.commons.tools.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.*;

@Slf4j
@Service
public class SearchService {

    @Autowired
    SearchWordRepository searchRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    DataRepository dataRepository;

    @Autowired
    WordRepository wordRepository;

    @Autowired
    EntityManager entityManager;

    //https://stackoverflow.com/questions/62164897/spring-data-jpa-how-to-implement-like-search-with-multiple-values-on-the-same

    public SearchRequest search( SearchRequest request ) {

        Pageable page = PageRequest.of(request.getPage() - 1, request.getRowsPerPage());
        List<String> words = prepareWordsToSearch(request.getSearch());
        List<String> wordsToSearch = prepareFindedWords(words);
        List<Long> ids = searchAllIds( request.getFromPath(), wordsToSearch, words.size(),page);
        List<FindedStorage> result = searchRepository.search( ids );
        request.setResult(result);
        request.setCount(new Long(result.size()));
        return request;

    }

    public SearchRequest searchIds( SearchRequest request ) {

        Pageable page = PageRequest.of(request.getPage() - 1, request.getRowsPerPage());
        List<String> words = prepareWordsToSearch(request.getSearch());
        List<String> wordsToSearch = prepareFindedWords(words);
        List<Long> ids = searchAllIds( request.getFromPath(), wordsToSearch, words.size(), page);
        request.setIds(ids);
        request.setCount(new Long(ids.size()));
        return request;

    }

    public static String collectionAsString(Collection<?> collection, String separator) {
        StringBuffer string = new StringBuffer(128);
        Iterator it = collection.iterator();

        while(it.hasNext()) {
            string.append("'")
                .append( it.next())
                .append("' ");
            if (it.hasNext()) {
                string.append(separator);
            }
        }

        return string.toString();
    }

    public List<Long> searchAllIds( String fromPath, List<String> wordsToSearch, int wordCount, Pageable page) {
        StringBuffer hql = new StringBuffer(128);

        hql
                .append( "select distinct \n" )
                .append( " s.id \n" )
                .append( "  from Storage s \n" )
                .append( " where s.path like ?1 \n")
                .append( " and ( select count(w) from Word w \n")
                .append( " where w.parent = s  \n")
                .append( "   and s.path like ?1  \n" )
                .append( "  and  w.word in ( " + collectionAsString(wordsToSearch, ",") + " ) \n")
                .append(" ) >= ?2")
        ;

        log.info(hql.toString());
        log.info("from:" + page.getPageNumber() * page.getPageSize());
        List<Long> ids = entityManager.createQuery(hql.toString())
                .setParameter(1,fromPath)
                .setParameter(2,new Long(wordCount))
                .setFirstResult( page.getPageNumber() * page.getPageSize() )
                .setMaxResults(page.getPageSize())
                .getResultList();

        return ids;
    }

    public SearchRequest searchIndex( SearchRequest request ) {

        List<Storage> storageList = storageRepository.dir(
                new StoragePathUtil(request.getFromPath())
                        .setRecursive(true)
                        .setShowFiles(true)
                        .setShowFolders(true));

        for( Storage storage : storageList ) {
            index(storage);
        }

        return request;

    }

    public SearchRequest searchIndexWithData( SearchRequest request ) {

        List<Storage> storageList = storageRepository.dir(
                new StoragePathUtil(request.getFromPath())
                        .setRecursive(true)
                        .setShowFiles(true)
                        .setShowFolders(true));

        for( Storage storage : storageList ) {
            index(storage);
            String data = dataRepository.getDataOf(storage.getPath());
        }

        return request;

    }
    private List<String> prepareWordsToSearch(String words ) {
        words = words.toLowerCase();
        words = words.replaceAll("'", "");
        words = words.replaceAll("\"", "");

        String[] allWords = words.split("\\s+");
        return Arrays.asList(allWords);
    }

    private List<String> prepareFindedWords(List<String> wordsToSearch ) {

        List<String> all = new ArrayList<>();
        for( String word : wordsToSearch ) {
            all.add(word);
            all.addAll(searchRepository.searchLike( "%" + word + "%" ));
        }

        return all;

    }

    public void index(Storage storage ) {
        String allWordsString = "";
        /*
        String[] paths = PathUtils.splitPath(storage.getPath().toLowerCase());
        for( String word : paths ) {
            String words[] = word.split("\\s+");
            for( String w : words )
                allWordsString += " " + w;
        }

         */
        allWordsString = storage.getPath().replaceAll("\\/", " ");
        if( storage.getDescription() != null ) {
            allWordsString += " " + storage.getDescription();
        }

        if( storage.getClazzName() != null ) {
            allWordsString += " " + storage.getClazzName().replaceAll("\\.", " ");
        }

        if( storage.getImage() != null ) {
            allWordsString += " " + storage.getImage().replaceAll("\\.", " ");
        }


        allWordsString = allWordsString.toLowerCase();
        allWordsString = allWordsString.replaceAll("'", "");
        allWordsString = allWordsString.replaceAll("á", "a");
        allWordsString = allWordsString.replaceAll("é", "e");
        allWordsString = allWordsString.replaceAll("í", "i");
        allWordsString = allWordsString.replaceAll("ó", "o");
        allWordsString = allWordsString.replaceAll("ú", "u");
        String[] allWords = allWordsString.split("\\s+");

        log.info("Index:" + allWordsString);
        wordRepository.deleteAll(storage);

        List<Word> wordsToSave = new ArrayList<>();

        for( String word : allWords ) {
            if( !word.isEmpty() ) {
                //log.info("Save word of path:" + word);
                Word storageWord = new Word();
                storageWord.setParent(storage);
                storageWord.setWord(word);
                wordsToSave.add(storageWord);
            }
        }

        index(allWords);
        wordRepository.saveAll(wordsToSave);

    }

    public void index( String[] words ) {
        List<GlobalWord> wordsToSave = new ArrayList<>();

        for( String word : words ) {
            if( !word.isEmpty() ) {
                GlobalWord globalWord = searchRepository.get(word);
                if (globalWord == null) {
                    globalWord = new GlobalWord();
                    globalWord.setWord(word);
                    wordsToSave.add(globalWord);
                }
            }
        }

        searchRepository.saveAll(wordsToSave);

    }
}
