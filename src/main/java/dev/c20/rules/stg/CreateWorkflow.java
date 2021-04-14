package dev.c20.rules.stg;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.c20.rules.engine.demo.WorkFlowBusiness;
import dev.c20.rules.engine.entities.BusinessRules;
import dev.c20.rules.engine.entities.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
@Order(1)
public class CreateWorkflow implements CommandLineRunner {

    static private final Logger logger = LoggerFactory.getLogger(CreateWorkflow.class);

    @Value("classpath:/static/resource.txt")
    private Resource resource;

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void run(String... args) throws Exception {

        logger.info("Run after app started");
        logger.info(asString(resource));

        BusinessRules bussinessRules = new BusinessRules();
        bussinessRules
                .name("Reglas de solicitudes")
                .startGroup()
                .name("Inicio")
                .factNotFound("GotoPorAtender")
                .finishGroup()

                .startGroup()
                .name("Por resolver")

                .startRule()
                .name("Regla para folder para aceptar")
                .addLine("context.accept == 1")
                    .startChildRule()
                    .name("Si es aceptada ademas y tiene definido un email")
                    .fact("GoToAceptarAndEmail")
                    .addLine("context.email != null")
                    .finishChildRule()

                    .startChildRule()
                    .name("Si es aceptada ademas y NO tiene email")
                    .fact("GoToAceptar")
                    .addLine("context.email == null")
                    .finishChildRule()

                .finishRule()

                .startRule()
                .name("Regla para folder para cancelar")
                .fact("GotoCancelar")
                .addLine("context.accept == 2")
                .finishRule()

                .finishGroup()

                .startGroup()
                .name("Por cancelar")
                .factNotFound( "GotoCancelar")
                .finishGroup()

        ;
/*
        List<RulesGroup> rulesGroupList = new ArrayList<>();
        RulesGroup rulesGroup = new RulesGroup()
                .name("Inicio")
                .defaultFact("GotoPorAtender")

                .startRule("Inicio")
                .fact("Nada")
                .finishRule()

                 */
                ;
        //rulesGroupList.add(rulesGroup);

        /*
        Rule rule = new Rule();
        rule.setName("Inicio");
        rule.setFact("GoToInicio");
        rulesGroup.setRules(new ArrayList<>());
        rulesGroup.getRules().add(rule);

        rulesGroupList.add(rulesGroup);

        rulesGroup = new RulesGroup();
        rulesGroup.setRules(new ArrayList<>());
        rulesGroup.setName("Resuelve");
        rule = new Rule();
        rule.setName("Por Anteder");
        rule.setFact("PorAtender");
        rule.setCondition( new ArrayList<>());
        rule.getCondition().add("context.data.accepted == 1");
        rule.getCondition().add("");
        rulesGroup.getRules().add(rule);

        rule = new Rule();
        rule.setName("Es cancelada");
        rule.setFact("Cancelada");
        rule.setCondition( new ArrayList<>());
        rule.getCondition().add("context.data.accepted != 1");
        rule.getCondition().add("");
        rulesGroup.getRules().add(rule);

        rulesGroupList.add(rulesGroup);

        rulesGroup = new RulesGroup();
        rulesGroup.setRules(new ArrayList<>());
        rulesGroup.setName("Cancelada");

        rule = new Rule();
        rule.setName("Es Borrada");
        rule.setFact("Borrada");
        rulesGroup.getRules().add(rule);

        rulesGroupList.add(rulesGroup);
        */

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(WorkFlowBusiness.getInstance().getBussinessRules()));

        Group rulesGroup = bussinessRules.find("Por resolver");
        System.out.println(objectMapper.writeValueAsString(rulesGroup));


    }

}
