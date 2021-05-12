package dev.c20.rules.search;

import dev.c20.rules.engine.services.entities.SearchRequest;
import dev.c20.rules.search.entities.GlobalWord;
import dev.c20.rules.storage.entities.Storage;
import dev.c20.rules.storage.entities.adds.Word;
import dev.c20.rules.search.repository.SearchWordRepository;
import dev.c20.rules.storage.repository.StorageRepository;
import dev.c20.rules.storage.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService {

    @Autowired
    SearchWordRepository globalWordRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    WordRepository wordRepository;


    //https://stackoverflow.com/questions/62164897/spring-data-jpa-how-to-implement-like-search-with-multiple-values-on-the-same

    public SearchRequest search( SearchRequest request ) {
        String words = request.getSearch();
        words = words.toLowerCase();
        words = words.replaceAll("'", "");
        words = words.replaceAll("\"", "");
        String[] allWords = words.split("\\s+");
        log.info(words);
        List<String> findedWords = new ArrayList<>();
        for( String word : allWords ) {
            log.info("search for " + "%" + word + "%");
            findedWords.addAll(  globalWordRepository.searchLike( "%" + word + "%" ) );
        }
        for( String word : findedWords ) {
            log.info(word);
        }

        if( request.getCount() == null ) {
            request.setCount(globalWordRepository.count());
        }
        Pageable firstPageWithTwoElements = PageRequest.of(request.getPage() - 1, request.getRowsPerPage());
        request.setResult(globalWordRepository.search( request.getFromPath(), findedWords, firstPageWithTwoElements ));
        return request;

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
                GlobalWord globalWord = globalWordRepository.get(word);
                if (globalWord == null) {
                    globalWord = new GlobalWord();
                    globalWord.setWord(word);
                    wordsToSave.add(globalWord);
                }
            }
        }

        globalWordRepository.saveAll(wordsToSave);

    }
}
