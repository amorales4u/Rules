package dev.c20.rules;

import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.services.BusinessStorageService;
import dev.c20.rules.engine.services.RulesAndFactsRegistered;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.repository.StorageRepository;
import dev.c20.workflow.commons.tools.PathUtils;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Order(2)
@Slf4j
public class ReadDemoWorkflow implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("classpath:/static/resource.txt")
    private Resource resource;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    private BusinessStorageService businessStorageService;

    @Override
    public void run(String... args) throws Exception {

        log.info("Load Business Rules for DEMO");
        log.info("Load FACTS");

        List<Storage> storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/facts/")
                .setRecursive(true)
                .setShowFiles(true)
                .setShowFolders(false) );

        storageList.stream()
                .filter( it -> it.getImage().equals("fact") )
                .forEach( (it) -> {
                    Fact fact = businessStorageService.readFactFromStorage(it);
                    RulesAndFactsRegistered.getInstance().register(fact);
                    log.info("Setting up Fact:" + fact.getName());
                }  );
/*
        for( Storage storage : storageList ) {
            Fact fact = businessStorageService.readFactFromStorage(storage);
            RulesAndFactsRegistered.getInstance().register(fact);
            log.info("Setting up Fact:" + fact.getName());
        }
*/

        // read rules for construct group business rule
        log.info("Load RULES");
        storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/rules/")
                        .setRecursive(true)
                        .setShowFiles(true)
                        .setShowFolders(false) );
        storageList.stream()
                .filter( it -> it.getImage().equals("rule") )
                .forEach( (it) -> {
                    Rule rule = businessStorageService.readRuleFromStorage(it);
                    RulesAndFactsRegistered.getInstance().register(rule);
                    log.warn( "Setting up Rule:" + rule.getName() );
                });

        // read all groups
        log.info("Load GROUPS of RULES");
        storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/groups/")
                        .setRecursive(false)
                        .setShowFiles(false)
                        .setShowFolders(true)
        );

        for( Storage storage : storageList ) {
            Group group = businessStorageService.readGroupFromStorage(storage);

            List<Storage> rulesOfGroup = storageRepository.dir(
                    new StoragePathUtil("/system/business/groups/" + storage.getName() + "/")
                            .setRecursive(true)
                            .setShowFiles(false)
                            .setShowFolders(true)
            );
            if( rulesOfGroup.size() == 0 ) {
                log.warn("Group " + group.getName() + " without configuration");
                group.setConfigured(false);
            } else {
                log.info("Setting up Group:  " + storage.getName() );
            }
            for( Storage storageRule : rulesOfGroup ) {
                int level = storageRule.getLevel() - 3;
                String divisor = level > 1 ? "+" : "";
                log.info("Rule               " + ( String.join("", Collections.nCopies(level-1,   "|    " ) )  + "|----" + storageRule.getName() ));
                Rule rule = RulesAndFactsRegistered.getInstance().getRule(storageRule.getName());
                String rulePath = PathUtils.getPathFromLevel(storageRule.getPath(),5);
                group.addTreeRule(rulePath, rule);

            }
            if( rulesOfGroup.size() > 0 ) {
                group.setConfigured(true);
                log.info("Group configured:  " + group.getName());
            }

            RulesAndFactsRegistered.getInstance().register(group);

        }


    }
    static public void main(String[] args ) {
        System.out.println("path:/system/SendFiles" );
        System.out.println("name:" + PathUtils.getName("/system/SendFiles") );
        System.out.println("path:/system/SendFiles/" );
        System.out.println("name:" + PathUtils.getName("/system/SendFiles/") );
    }
}
