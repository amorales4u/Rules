package dev.c20.rules;

import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.Rule;
import dev.c20.rules.engine.services.BusinessRulesService;
import dev.c20.rules.engine.services.FactsRegistered;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
@Slf4j
public class ReadDemoWorkflow implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("classpath:/static/resource.txt")
    private Resource resource;

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    private BusinessRulesService businessRulesService;

    @Override
    public void run(String... args) throws Exception {

        log.info("Load Business Rules for DEMO");
        log.info("Load FACTS");
        //createTreeFolders( "/system/business/facts/", "System facts for rules");
        //createTreeFolders( "/system/business/rules/", "System rules");
        //createTreeFolders( "/system/business/groups/", "System Business Groups");

        List<Storage> storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/facts/")
                .setRecursive(true)
                .setShowFiles(true)
                .setShowFolders(false) );

        for( Storage storage : storageList ) {
            Fact fact = businessRulesService.readFactFromStorage(storage);
            FactsRegistered.getInstance().register(fact);
        }

        // read rules for construct group business rule
        storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/rules/")
                        .setRecursive(true)
                        .setShowFiles(true)
                        .setShowFolders(false) );

        Map<String,Rule> rules = new HashMap<>();
        for( Storage storage : storageList ) {
            Rule rule = businessRulesService.readRuleFromStorage(storage);
            log.warn( "s)Rule:" + storage.getName() );
            log.warn( "r)Rule:" + rule.getName() );
            rules.put(rule.getName(), rule);
        }

        // read all groups
        storageList = storageRepository.dir(
                new StoragePathUtil("/system/business/groups/")
                        .setRecursive(false)
                        .setShowFiles(false)
                        .setShowFolders(true)
        );

        for( Storage storage : storageList ) {
            log.info("Group:" + storage.getName() );
            Group group = businessRulesService.readGroupFromStorage(storage);

            List<Storage> rulesOfGroup = storageRepository.dir(
                    new StoragePathUtil("/system/business/groups/" + storage.getName() + "/")
                            .setRecursive(true)
                            .setShowFiles(false)
                            .setShowFolders(true)
            );

            for( Storage storageRule : rulesOfGroup ) {
                log.info("rule (level)[name]: (" + storageRule.getLevel() + ")[" +storageRule.getName() + "] " + storageRule.getPath());
                Rule rule = rules.get(storageRule.getName());
                String rulePath = PathUtils.getPathFromLevel(storageRule.getPath(),5);
                group.addTreeRule(rulePath, rule);

            }

            log.info("Group configured:" + group.getName());

            WorkFlowBusiness.getInstance().getBussinessRules().getRulesGroups().add(group);

        }

        WorkFlowBusiness.getInstance().getBussinessRules().name("Reglas de ejemplo");

    }
    static public void main(String[] args ) {
        System.out.println("path:/system/SendFiles" );
        System.out.println("name:" + PathUtils.getName("/system/SendFiles") );
        System.out.println("path:/system/SendFiles/" );
        System.out.println("name:" + PathUtils.getName("/system/SendFiles/") );
    }
}
