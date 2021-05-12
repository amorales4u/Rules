package dev.c20.rules.engine.services;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.MapRuleToFact;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.entities.adds.Value;
import dev.c20.rules.engine.storage.repository.StorageRepository;
import dev.c20.rules.engine.storage.repository.ValueRepository;
import dev.c20.workflow.commons.tools.PathUtils;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BusinessStorageService {
    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ValueRepository valueRepository;

    @Autowired
    GlobalWordsService globalWordsService;

    public void createTreeFolders(String treePath , String description) {
        try {
            String[] paths = PathUtils.splitPath(treePath);
            String path = "/";
            Storage storage;
            for( String p : paths ) {
                path += p + "/";
                storage = storageRepository.getFolder(path);
                if( storage != null ) {
                    continue;
                } else {
                    log.info("Creating folder Path " + path );
                }
                storage = new Storage();
                storage.setPath(path);
                storage.setDescription(description);
                storageRepository.save(storage);
            }

        } catch( Exception ex ) {
            log.error("Error al crear el path:", ex);
        }

    }

    public Storage persistFact(Fact fact) {
        String path = fact.getPath();
        fact.setName( PathUtils.getName(path));
        Storage storage = storageRepository.getFile(path);

        if( storage == null ) {
            storage = new Storage();
            storage.setPath(path);
            log.info("Fact not found (add):" + path);
        } else {
            log.info("Fact found (update):" + path);
        }
        storage.setDescription(fact.getDescription());
        storage.setClazzName(fact.getClazzName());
        storage.setImage("fact");
        storageRepository.save(storage);

        valueRepository.deleteAll( storage );
        List<Value> values = new ArrayList<>();
        int idx = 0;
        String strIdx = "";
        for( String param : fact.getParameters() ) {
            idx++;
            if( idx < 9 ) {
                strIdx = "0" + idx;
            } else {
                strIdx = idx + "";
            }
            Value value = new Value();
            value.setParent(storage);
            value.setName("param." + strIdx);
            value.setValue(param);
            values.add(value);
        }

        for( String key : fact.getProperties().keySet() ) {
            Value value = new Value();
            value.setParent(storage);
            value.setName(key);
            value.setValue(fact.getProperties().get(key));
            values.add(value);
        }

        valueRepository.saveAll(values);

        globalWordsService.saveWords(storage);

        return storage;
    }

    public Fact readFactFromStorage(Storage storage) {
        if( storage == null ) {
            return null;
        }
        Fact fact = new Fact();
        List<Value> values = valueRepository.getAll(storage);

        fact.setName(storage.getName())
                .setPath(storage.getPath())
                .setClazzName(storage.getClazzName())
                ;
        for( Value value : values ) {
            if( value.getName().startsWith("param."))
                fact.addParameter(value.getValue());
            else 
                fact.addProperty(value.getName(),value.getValue());
        }

        return fact;

    }

    public Fact readFact(String factName){
        String path = factName;

        Storage storage = storageRepository.getFile(path);

        return readFactFromStorage(storage);

    }

    public Storage persistGroup(Group group) {
        String path = group.getPath() ;


        Storage storage = storageRepository.getFolder(path);

        if( storage == null ) {
            storage = new Storage();
            storage.setPath(path);
            log.info("Group not found (add):" + path);
        } else {
            log.info("Group found (update):" + path);
        }

        storage.setClazzName(group.getClass().getName());
        storage.setDescription(group.getDescription());
        storage.setImage("group");
        storageRepository.save(storage);

        valueRepository.deleteAll( storage );
        Value value = new Value();
        value.setParent(storage);
        value.setName("factNotFound");
        value.setValue(group.getFactNotFound());
        valueRepository.save(value);

        value = new Value();
        value.setParent(storage);
        value.setName("factNotFoundMessage");
        value.setValue(group.getFactNotFoundMessage());
        valueRepository.save(value);

        globalWordsService.saveWords(storage);

        return storage;
    }

    public Group readGroupFromStorage( Storage storage) {
        if (storage == null) {
            return null;
        }
        Group group = new Group();
        group.name(storage.getName());
        group.setPath(storage.getPath());
        group.description(storage.getDescription());

        List<Value> values = valueRepository.getAll(storage);

        for( Value value : values ) {
            if( value.getName().equals( "factNotFound") ) {
                group.factNotFound( value.getValue() );
            } else if( value.getName().equals( "factNotFoundMessage") ) {
                group.factNotFoundMessage(value.getValue());
            }

        }

        return group;

    }

    public Group readGroup(String groupName) {

        Storage storage = storageRepository.getFile(groupName);

        return readGroupFromStorage(storage);
    }

    public Storage persistRule(Rule rule) {
        String path = rule.getPath();
        rule.setName( PathUtils.getName(path));


        Storage storage = storageRepository.getFile(path);

        if( storage == null ) {
            storage = new Storage();
            storage.setPath(path);
            log.info("Rule not found  (add):" + path);
        } else {
            log.info("Rule found (update):" + path);
        }
        storage.setClazzName(rule.getClass().getName());
        storage.setDescription(rule.getDescription());
        storage.setVisible(rule.isExclusive());
        storage.setImage("rule");
        storageRepository.save(storage);

        valueRepository.deleteAll( storage );
        List<Value> values = new ArrayList<>();
        Value value;

        for( String line : rule.getCondition() ) {
            value = new Value();
            value.setParent(storage);
            value.setName("line");
            value.setValue(line);
            values.add(value);
        }

        if( rule.getFact() != null ) {
            value = new Value();
            value.setParent(storage);
            value.setName("MapRuleToFact.name");
            value.setValue(rule.getFact().getName());
            values.add(value);
            for (String key : rule.getFact().getParameters().keySet()) {
                value = new Value();
                value.setParent(storage);
                value.setName("MapRuleToFact.param." + key);
                value.setValue(rule.getFact().getParameters().get(key));
                values.add(value);
            }
        }

        valueRepository.saveAll(values);

        globalWordsService.saveWords(storage);

        return storage;
    }

    public Rule readRuleFromStorage( Storage storage ) {

        if( storage == null ) {
            log.info("Rule not found");
            return null;
        }
        Rule rule = new Rule();
        rule.setName(storage.getName());
        rule.setDescription(storage.getDescription());
        rule.setPath(storage.getPath());
        rule.setExclusive( storage.getVisible() );

        List<Value> values = valueRepository.getAll(storage);

        for( Value value : values ) {
            String valueName = value.getName();
            if( valueName.equals("line")) {
                rule.addLine(value.getValue());
            } else if( valueName.equals("MapRuleToFact.name") ) {
                if( rule.getFact() == null ) {
                    rule.setFact( new MapRuleToFact() );
                }
                rule.getFact().setName(value.getValue());
            } else if( valueName.startsWith("MapRuleToFact.param.")) {
                rule.getFact().addParameter(valueName.substring(20),value.getValue());
            }
        }

        return rule;

    }

    public Rule readRule( String name) {
        String path =  name;

        Storage storage = storageRepository.getFile(path);
        return readRuleFromStorage(storage);
    }

    public Storage setRulesForGroup(String groupPath, List<String> rules) {

        Storage storage = storageRepository.getFolder(groupPath);
        if (storage == null) {
            log.error( "Group not exists " + groupPath);
            return null;
        }
        int deletedCount = storageRepository.deleteChilden(
                new StoragePathUtil(groupPath)
        );

        log.warn( "Eliminated " + deletedCount + " record(s)");


        for( String rule : rules ) {
            String rulePath = groupPath + rule + "/";
            log.info( "Adding rule:" + rulePath);
            Storage folder = new Storage();
            folder.setPath(rulePath);
            folder.setImage("group-rule");
            storageRepository.save(folder);
        }

        return storage;

    }

}
