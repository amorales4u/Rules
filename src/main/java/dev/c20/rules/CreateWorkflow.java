package dev.c20.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.demo.facts.GroovyFactService;
import dev.c20.rules.engine.demo.facts.GroovyStringFactService;
import dev.c20.rules.engine.entities.BusinessRules;
import dev.c20.rules.engine.entities.Fact;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.Storage;
import dev.c20.rules.engine.repositories.StorageRepository;
import dev.c20.rules.engine.services.FactsRegistered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
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
public class CreateWorkflow implements CommandLineRunner {

    @Value("classpath:/static/resource.txt")
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

    @Override
    public void run(String... args) throws Exception {

        log.info("Run after app started");
        log.info(asString(resource));


        BusinessRules businessRules = WorkFlowBusiness.getInstance().getBussinessRules();
        ObjectMapper objectMapper = new ObjectMapper();
        log.info(objectMapper.writeValueAsString(businessRules));

        Group rulesGroup = businessRules.find("Por resolver");
        log.info(objectMapper.writeValueAsString(rulesGroup));

        List<Storage> storages = storageRepository.dir( "/Catálogos/Facts/%", 2 );

        log.info(storages.toString());

        FactsRegistered.getInstance().register( new Fact()
                        .name("GoToAceptar")
                        .description("Mueve la tarea a la carpeta de Aceptar")
                        .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        FactsRegistered.getInstance().register( new Fact()
                .name("GotoPorAtender")
                .description("Mueve la tarea a la carpeta por atender")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        FactsRegistered.getInstance().register( new Fact()
                .name("SendEmail")
                .description("Manda un correo")
                .addParameter("email")
                .addParameter("to")
                .addParameter("subject")
                .addParameter("body")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        FactsRegistered.getInstance().register( new Fact()
                .name("GotoCancelar")
                .description("Manda la tarea a la carpeta de cancelar")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyFactService") );

        FactsRegistered.getInstance().register( new Fact()
                .name("NoHayFactPorResolver")
                .description("Regresa un error pues no se resuelve la carpeta")
                .clazzName("dev.c20.rules.engine.demo.facts.GroovyStringFactService") );


    }

}
