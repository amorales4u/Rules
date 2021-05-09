package dev.c20.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.entities.*;
import dev.c20.rules.engine.services.BusinessStorageService;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.repository.StorageRepository;
import dev.c20.workflow.commons.tools.PathUtils;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    //@org.springframework.beans.factory.annotation.Value("classpath:/static/resource.txt")
    //private Resource resource;

    @Value("${business-path}")
    String businessPath;

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
    private BusinessStorageService businessStorageService;

    @Override
    public void run(String... args) throws Exception {

        String factsPath = businessPath + "facts/";
        String rulesPath = businessPath + "rules/";
        String groupsPath = businessPath + "groups/";

        log.info("Configure DEMO Business Rules, Rules, Groups and Facts");
        log.info(businessPath);

        log.info("Creating business tree for persist");
        log.info("/system/business/facts/");
        log.info( "/system/business/rules/", "System rules");
        log.info( "/system/business/groups/", "System Business Groups");

        businessStorageService.createTreeFolders(factsPath, "Facts");
        businessStorageService.createTreeFolders(rulesPath, "Rules");
        businessStorageService.createTreeFolders(groupsPath, "Groups");


        ObjectMapper objectMapper = new ObjectMapper();

        businessStorageService.persistFact(new Fact()
                .setName("GoToAceptar")
                .setDescription("Mueve la tarea a la carpeta de Aceptar")
                .setPath(factsPath)
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService"));

        businessStorageService.persistFact( new Fact()
                        .setName("GoToAceptar")
                        .setDescription("Mueve la tarea a la carpeta de Aceptar")
                        .setPath(factsPath)
                        .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setName("GotoPorAtender")
                .setDescription("Mueve la tarea a la carpeta por atender")
                .setPath(factsPath)
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setName("SendEmail")
                .setDescription("Manda un correo")
                .setPath(factsPath)
                .addParameter("email")
                .addParameter("to")
                .addParameter("subject")
                .addParameter("body")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setName("RestStorage")
                .setDescription("Manda un correo")
                .setPath(factsPath)
                .addParameter("[")
                .addParameter("context: null,")
                .addParameter("service: null,")
                .addParameter("path: '',")
                .addParameter("request: [")
                .addParameter("path:null,")
                .addParameter("body:null")
                .addParameter("]")
                .addParameter("]")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setName("GotoCancelar")
                .setDescription("Manda la tarea a la carpeta de cancelar")
                .setPath(factsPath)
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setName("NoHayFactPorResolver")
                .setDescription("Regresa un error pues no se resuelve la carpeta")
                .setPath(factsPath)
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyStringFactService") );

        businessStorageService.persistGroup(new Group()
                .setName("Inicio")
                .setPath(groupsPath)
                .setFactNotFound("GotoPorAtender"));

        businessStorageService.persistGroup(new Group()
                .setName("Por resolver")
                .setDescription("Reglas para Mover una tarea que esta en 'Por Resolver'")
                .setPath(groupsPath)
                .setFactNotFound("NoHayFactPorResolver")
                .setFactNotFoundMessage("Para mover la tarea es necesario que aceptada sea 1 o 2, " +
                        " y si desea mandar un email lo tiene que indicar.\n" +
                        " Los valores enviados son: aceptada=[$context.accept] email=[$context.email]")
        );

        businessStorageService.persistGroup(new Group()
                .setName("Por cancelar")
                .setPath(groupsPath)
                .setFactNotFound( "GotoCancelar"));

        businessStorageService.persistRule(new Rule()
                .setName("Enviar tarea a Aceptar")
                .setPath(rulesPath)
                .setExclusive(false)
                .addLine("context.accept == 1"));

        businessStorageService.persistRule(new Rule()
                        .setName("Es aceptada y tiene definido un email")
                        .setPath(rulesPath)
                        .addLine("context.email != null")
                        .setExclusive(false)
                        .setFact( new MapRuleToFact()
                                .name("RestStorage")
                                .addParameter("context.param.context","'/workflow/rest/storage/list'")
                                .addParameter("context.param.service", "'storage/list'" )
                                .addParameter("context.param.path", "null" )
                                .addParameter("context.param.request.path", "'/system/business/facts/'" )
                                .addParameter("context.param.request.body", "context.body" )
                        )
                        );
        businessStorageService.persistRule(new Rule()
                .setName("Es aceptada y NO tiene email")
                .setPath(rulesPath)
                .addLine("context.email == null")
                .setExclusive(false)
                .setFact(new MapRuleToFact()
                        .name("GoToAceptar")
                        .addParameter("taskName", "context.taskName")
                        .addParameter("pathToMove", "context.pathToMove")
                )
        );

        businessStorageService.persistRule(new Rule()
                .setName("Mover tarea a Cancelar")
                .addLine("context.accept == 2")
                .setPath(rulesPath)
                .setExclusive(false)
                .setFact(new MapRuleToFact()
                        .name("GoToCancelar")
                        .addParameter("taskName", "context.taskName")
                        .addParameter("pathToMove", "context.pathToMove"))
        );


        businessStorageService.persistRule(new Rule()
                        .setName("Cancela una tarea")
                        .setPath(rulesPath)
                        .setExclusive(false)
                        .addLine("context.accept == 1")
                        .setFact(new MapRuleToFact()
                                .name("GoToAceptar")
                                .addParameter("taskName", "context.taskName")
                                .addParameter("pathToMove", "context.pathToMove"))
        );


        businessStorageService.setRulesForGroup( "/system/business/groups/Por resolver/",
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
