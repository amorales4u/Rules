package dev.c20.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.BusinessRules;
import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.storage.entities.Storage;
import dev.c20.rules.engine.storage.entities.adds.Value;
import dev.c20.rules.engine.storage.repository.StorageRepository;
import dev.c20.rules.engine.services.FactsRegistered;
import dev.c20.rules.engine.storage.repository.ValueRepository;
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

@Component
@Order(1)
@Slf4j
public class CreateWorkflow implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("classpath:/static/resource.txt")
    private Resource resource;

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Autowired
    StorageRepository storageRepository;

    @Autowired
    ValueRepository valueRepository;

    private Storage addFolder( String path , String description) {
        try {
            Storage storage = new Storage();
            storage.setPath(path);
            storage.setDescription("System info");
            storageRepository.save(storage);
            return storage;
        } catch( Exception ex ) {
            log.error("Error al crear el path:", ex);
            return null;
        }

    }
    private void addTreeFact() {

        addFolder( "/system/", "System information");
        addFolder( "/system/business/", "System business rules and facts");
        addFolder( "/system/business/facts/", "System facts for rules");
        addFolder( "/system/business/rules/", "System rules");


    }
    private Storage persistFact(Fact fact) {
        String path = "/system/business/facts/" + ( fact.category() != null ? fact.category() + "/" : "" )  + fact.name();
        if( fact.category() != null ) {
            addFolder( path, "Fact category " + fact.category());
        }

        Storage storage = storageRepository.getFile(path);

        if( storage == null ) {
            storage = new Storage();
            storage.setPath(path);
            log.info("File not found:" + path);
        } else {
            log.info("File found:" + path);
        }
        storage.setDescription(fact.description());
        storage.setClazzName(fact.clazzName());
        storageRepository.save(storage);

        valueRepository.deleteAll( storage );

        for( String param : fact.parameters() ) {
            Value value = new Value();
            value.setParent(storage);
            value.setName(param);
            value.setValue("param");
            valueRepository.save(value);
        }

        return null;
    }

    @Override
    public void run(String... args) throws Exception {

        log.info("Run after app started");
        log.info(asString(resource));

        log.info("Creating business tree for persist");
        addTreeFact();


        BusinessRules businessRules = WorkFlowBusiness.getInstance().getBussinessRules();
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(businessRules));

        Group rulesGroup = businessRules.find("Por resolver");
        log.info(objectMapper.writeValueAsString(rulesGroup));

        persistFact(new Fact()
                .name("GoToAceptar")
                .description("Mueve la tarea a la carpeta de Aceptar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService"));

        persistFact( new Fact()
                        .name("GoToAceptar")
                        .description("Mueve la tarea a la carpeta de Aceptar")
                        .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        persistFact( new Fact()
                .name("GotoPorAtender")
                .description("Mueve la tarea a la carpeta por atender")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        persistFact( new Fact()
                .name("SendEmail")
                .description("Manda un correo")
                .addParameter("email")
                .addParameter("to")
                .addParameter("subject")
                .addParameter("body")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        persistFact( new Fact()
                .name("GotoCancelar")
                .description("Manda la tarea a la carpeta de cancelar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        persistFact( new Fact()
                .name("NoHayFactPorResolver")
                .description("Regresa un error pues no se resuelve la carpeta")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyStringFactService") );

        /*
        FactsRegistered.getInstance().register( new Fact()
                .name("GoToAceptar")
                .description("Mueve la tarea a la carpeta de Aceptar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

         */
    }

}
