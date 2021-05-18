package dev.c20.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.entities.*;
import dev.c20.rules.engine.services.BusinessStorageService;
import dev.c20.rules.storage.entities.Storage;
import dev.c20.rules.storage.repository.StorageRepository;
import dev.c20.workflow.commons.tools.PathUtils;
import dev.c20.workflow.commons.tools.StoragePathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    @Autowired
    private ServletWebServerApplicationContext server;

    @Override
    public void run(String... args) throws Exception {

        String factsPath = businessPath + "facts/";
        String rulesPath = businessPath + "rules/";
        String groupsPath = businessPath + "groups/";

        log.info("Configure DEMO Business Rules, Rules, Groups and Facts");
        log.info(businessPath);
        log.warn("Server port:" + server.getWebServer().getPort()+"");

        log.info("Creating business tree for persist");
        log.info("/system/business/facts/");
        log.info( "/system/business/rules/", "System rules");
        log.info( "/system/business/groups/", "System Business Groups");

        businessStorageService.createTreeFolders(factsPath, "Facts");
        businessStorageService.createTreeFolders(rulesPath, "Rules");
        businessStorageService.createTreeFolders(groupsPath, "Groups");


        ObjectMapper objectMapper = new ObjectMapper();

        businessStorageService.persistFact(new Fact()
                .setPath( factsPath + "GoToAceptar")
                .setDescription("Mueve la tarea a la carpeta de Aceptar")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService"));

        businessStorageService.persistFact( new Fact()
                        .setPath(factsPath + "GoToAceptar")
                        .setDescription("Mueve la tarea a la carpeta de Aceptar")
                        .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setPath(factsPath + "GotoPorAtender")
                .setDescription("Mueve la tarea a la carpeta por atender")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setPath(factsPath + "SendEmail")
                .setDescription("Manda un correo")
                .addParameter("email")
                .addParameter("to")
                .addParameter("subject")
                .addParameter("body")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setPath(factsPath + "RestStorage")
                .setDescription("Manda un correo")
                .addParameter("[")
                .addParameter("context: null,")
                .addParameter("service: null,")
                .addParameter("path: '',")
                .addParameter("request: [")
                .addParameter("path:null,")
                .addParameter("body:null")
                .addParameter("]")
                .addParameter("]")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService")
                .addProperty("source", "resource")
                .addProperty("source", "file")
                .addProperty("source", "rest")
                .addProperty("source", "data")
                .addProperty("resource", "c:/file.groovy")
                .addProperty("resource", "/Workflow/storage/data"+factsPath+"RestStorage")
        );

        businessStorageService.persistFact( new Fact()
                .setPath(factsPath + "GotoCancelar")
                .setDescription("Manda la tarea a la carpeta de cancelar")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        businessStorageService.persistFact( new Fact()
                .setPath(factsPath + "NoHayFactPorResolver")
                .setDescription("Regresa un error pues no se resuelve la carpeta")
                .setClazzName("dev.c20.rules.engine.demo.facts.GroovyStringFactService") );

        businessStorageService.persistGroup(new Group()
                .setPath(groupsPath + "Inicio/")
                .setFactNotFound("GotoPorAtender"));

        businessStorageService.persistGroup(new Group()
                .setPath(groupsPath + "Por resolver/")
                .setDescription("Reglas para Mover una tarea que esta en 'Por Resolver'")
                .setFactNotFound("NoHayFactPorResolver")
                .setFactNotFoundMessage("Para mover la tarea es necesario que aceptada sea 1 o 2, " +
                        " y si desea mandar un email lo tiene que indicar.\n" +
                        " Los valores enviados son: aceptada=[$context.accept] email=[$context.email]")
        );

        businessStorageService.persistGroup(new Group()
                .setPath(groupsPath + "Por cancelar/")
                .setFactNotFound( "GotoCancelar"));

        businessStorageService.persistRule(new Rule()
                .setPath(rulesPath + "Enviar tarea a Aceptar")
                .setExclusive(false)
                .addLine("context.accept == 1"));

        businessStorageService.persistRule(new Rule()
                        .setPath(rulesPath + "Es aceptada y tiene definido un email")
                        .addLine("context.email != null")
                        .setExclusive(false)
                        .setFact( new MapRuleToFact()
                                .name("RestStorage")
                                .addParameter("context.param.context","'/workflow/rest/storage/list'")
                                .addParameter("context.param.service", "'storage/list'" )
                                .addParameter("context.param.path", "null" )
                                .addParameter("context.param.request.path", "'/system/business/facts/'" )
                                .addParameter("context.param.request.body", "context.email" )
                        )
                        );
        businessStorageService.persistRule(new Rule()
                .setPath(rulesPath + "Es aceptada y NO tiene email")
                .addLine("context.email == null")
                .setExclusive(false)
                .setFact(new MapRuleToFact()
                        .name("GoToAceptar")
                        .addParameter("taskName", "context.taskName")
                        .addParameter("pathToMove", "context.pathToMove")
                )
        );

        businessStorageService.persistRule(new Rule()
                .setPath(rulesPath + "Mover tarea a Cancelar")
                .addLine("context.accept == 2")
                .setExclusive(false)
                .setFact(new MapRuleToFact()
                        .name("GoToCancelar")
                        .addParameter("taskName", "context.taskName")
                        .addParameter("pathToMove", "context.pathToMove"))
        );


        businessStorageService.persistRule(new Rule()
                .setPath(rulesPath + "Cancela una tarea")
                .setExclusive(false)
                .addLine("context.accept == 1")
                .setFact(new MapRuleToFact()
                        .name("GoToAceptar")
                        .addParameter("taskName", "context.taskName")
                        .addParameter("pathToMove", "context.pathToMove"))
        );


        List<String> rules = new ArrayList<>();
        rules.add("Enviar tarea a Aceptar");
        rules.add( "Enviar tarea a Aceptar/Es aceptada y tiene definido un email");
        rules.add("Enviar tarea a Aceptar/Es aceptada y NO tiene email");
        rules.add("Mover tarea a Cancelar");
        rules.add("Mover tarea a Cancelar/Cancela una tarea");
        businessStorageService.setRulesForGroup( "/system/business/groups/Por resolver/",
            rules);

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
