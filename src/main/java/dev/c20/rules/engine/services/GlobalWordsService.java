package dev.c20.rules.engine.services;

import dev.c20.rules.engine.storage.entities.GlobalWord;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.entities.adds.Word;
import dev.c20.rules.engine.storage.repository.GlobalWordRepository;
import dev.c20.rules.engine.storage.repository.StorageRepository;
import dev.c20.rules.engine.storage.repository.WordRepository;
import dev.c20.workflow.commons.tools.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GlobalWordsService {

    @Autowired
    GlobalWordRepository globalWordRepository;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    WordRepository wordRepository;


    //https://stackoverflow.com/questions/62164897/spring-data-jpa-how-to-implement-like-search-with-multiple-values-on-the-same
    public void saveWords(Storage storage ) {
        String[] paths = PathUtils.splitPath(storage.getPath().toLowerCase());
        String allWordsString = "";
        for( String word : paths ) {
            String words[] = word.split("\\s+");
            for( String w : words )
                allWordsString += " " + w;
        }
        if( storage.getDescription() != null ) {
            allWordsString += " " + storage.getDescription();
        }
        allWordsString = allWordsString.toLowerCase();
        allWordsString = allWordsString.replaceAll("'", "");
        String[] allWords = allWordsString.split("\\s+");

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

        saveWords(allWords);
        wordRepository.saveAll(wordsToSave);

    }

    public void saveWords( String[] words ) {
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
