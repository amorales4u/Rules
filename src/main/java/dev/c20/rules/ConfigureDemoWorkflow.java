package dev.c20.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.*;
import dev.c20.rules.engine.services.BusinessRulesService;
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
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(1)
@Slf4j
public class ConfigureDemoWorkflow implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("classpath:/static/resource.txt")
    private Resource resource;

    @Autowired
    StorageRepository storageRepository;

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Autowired
    private BusinessRulesService businessRulesService;

    @Override
    public void run(String... args) throws Exception {

        log.info("Configure DEMO Business Rules, Rules, Groups and Facts");
        log.info(asString(resource));

        log.info("Creating business tree for persist");
        log.info("/system/business/facts/");
        log.info( "/system/business/rules/", "System rules");
        log.info( "/system/business/groups/", "System Business Groups");

        businessRulesService.addTreeFact();


        BusinessRules businessRules = WorkFlowBusiness.getInstance().getBussinessRules();
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(businessRules));

        Group rulesGroup = businessRules.find("Por resolver");
        log.info(objectMapper.writeValueAsString(rulesGroup));

        businessRulesService.persistFact(new Fact()
                .name("GoToAceptar")
                .description("Mueve la tarea a la carpeta de Aceptar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService"));

        businessRulesService.persistFact( new Fact()
                        .name("GoToAceptar")
                        .description("Mueve la tarea a la carpeta de Aceptar")
                        .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessRulesService.persistFact( new Fact()
                .name("GotoPorAtender")
                .description("Mueve la tarea a la carpeta por atender")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessRulesService.persistFact( new Fact()
                .name("SendEmail")
                .description("Manda un correo")
                .addParameter("email")
                .addParameter("to")
                .addParameter("subject")
                .addParameter("body")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessRulesService.persistFact( new Fact()
                .name("RestStorage")
                .description("Manda un correo")
                .addParameter("[")
                .addParameter("context: null,")
                .addParameter("service: null")
                .addParameter("path: ''")
                .addParameter("request: [")
                .addParameter("path:null")
                .addParameter("body:null")
                .addParameter("]")
                .addParameter("]")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessRulesService.persistFact( new Fact()
                .name("GotoCancelar")
                .description("Manda la tarea a la carpeta de cancelar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessRulesService.persistFact( new Fact()
                .name("NoHayFactPorResolver")
                .description("Regresa un error pues no se resuelve la carpeta")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyStringFactService") );

        businessRulesService.persistGroup(new Group()
                .name("Inicio")
                .factNotFound("GotoPorAtender"));

        businessRulesService.persistGroup(new Group()
                .name("Por resolver")
                .description("Reglas para Mover una tarea que esta en 'Por Resolver'")
                .factNotFound("NoHayFactPorResolver")
                .factNotFoundMessage("Para mover la tarea es necesario que aceptada sea 1 o 2, " +
                        " y si desea mandar un email lo tiene que indicar.\n" +
                        " Los valores enviados son: aceptada=[$context.accept] email=[$context.email]")
        );

        businessRulesService.persistGroup(new Group()
                .name("Por cancelar")
                .factNotFound( "GotoCancelar"));

        businessRulesService.persistRule(new Rule()
                .name("Enviar tarea a Aceptar")
                .exclusive(false)
                .addLine("context.accept == 1"));

        businessRulesService.persistRule(new Rule()
                        .name("Es aceptada y tiene definido un email")
                        .addLine("context.email != null")
                        .exclusive(false)
                        .fact( new MapRuleToFact()
                                .name("SendEmail")
                                .addParameter("email","context.email")
                                .addParameter("to", "context.to" )
                                .addParameter("subject", "context.subject" )
                                .addParameter("body", "context.body" )
                        )
                        );
        businessRulesService.persistRule(new Rule()
                        .name("Es aceptada y NO tiene email")
                        .addLine("context.email == null")
                        .exclusive(false)
                        .fact(new MapRuleToFact()
                                .name("GoToAceptar")
                                .addParameter("taskName", "context.taskName")
                                .addParameter("pathToMove", "context.pathToMove")
                        )
        );

        businessRulesService.persistRule(new Rule()
                        .name("Mover tarea a Cancelar")
                        .addLine("context.accept == 2")
                        .exclusive(false)
                        .fact(new MapRuleToFact()
                                .name("GoToCancelar")
                                .addParameter("taskName", "context.taskName")
                                .addParameter("pathToMove", "context.pathToMove"))
        );


        businessRulesService.persistRule(new Rule()
                        .name("Cancela una tarea")
                        .exclusive(false)
                        .addLine("context.accept == 1")
                        .fact(new MapRuleToFact()
                                .name("GoToAceptar")
                                .addParameter("taskName", "context.taskName")
                                .addParameter("pathToMove", "context.pathToMove"))
        );


        businessRulesService.setRulesForGroup( "/system/business/groups/Por resolver/",
            "Enviar tarea a Aceptar",
            "Enviar tarea a Aceptar/Es aceptada y tiene definido un email",
            "Enviar tarea a Aceptar/Es aceptada y NO tiene email",
            "Mover tarea a Cancelar",
            "Mover tarea a Cancelar/Cancela una tarea"


        );

        List<Storage> dir = storageRepository.dir(
                new StoragePathUtil("/system/business/groups/Por resolver/")
                        .setShowFolders(false)
                        .setShowFiles(true)
                .setRecursive(true)
        );
        for( Storage storage : dir ) {
            log.info(storage.getPath());
        }
        /*
        FactsRegistered.getInstance().register( new Fact()
                .name("GoToAceptar")
                .description("Mueve la tarea a la carpeta de Aceptar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

         */
    }

    public static void main(String[] args)  {
        String path = "/workflow/storage/file/asasas";
        System.out.println(path);
        System.out.println(PathUtils.getPathFromLevel(path,4));
        System.out.println(getName(PathUtils.getPathFromLevel(path,4)));
        // path level: /0/1/
        path = "/Workflows/";
        System.out.println(path);
        System.out.println(PathUtils.getParentFolder( path));
        System.out.println(PathUtils.getPathLevel( path));

        path = "/Workflows/EUC-27/Catalogos/";
        System.out.println(path);
        System.out.println(getName(path));
        System.out.println(PathUtils.getPathLevel( path));
        System.out.println(PathUtils.getParentFolder( path));
        System.out.println(PathUtils.getPathPart( path, 2));
        System.out.println(PathUtils.getPathNameFromLevel( path, 0));
        System.out.println(PathUtils.getPathNameFromLevel( path, 1));
        System.out.println(PathUtils.getPathNameFromLevel( path, 2));

    }

    static public String getPathNameFromLevel(String resource, int level) {
        return PathUtils.splitPath(resource)[level];
    }
    static public String getName(String resource) {

        if ("/".equals(resource)) {
            return "/";
        }
        // remove the last char, for a folder this will be "/", for a file it does not matter
        String parent = (resource.substring(0, resource.length() - 1));
        // now as the name does not end with "/", check for the last "/" which is the parent folder name
        return parent.substring(parent.lastIndexOf('/') + 1);
    }


}
