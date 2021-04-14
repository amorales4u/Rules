package dev.c20.rules.engine.demo;

import dev.c20.rules.engine.entities.BusinessRules;

public class WorkFlowBusiness {

    static private WorkFlowBusiness instance = null;

    private BusinessRules bussinessRules;

    static final public WorkFlowBusiness getInstance() {
        if( instance == null ) {
            instance = new WorkFlowBusiness();
        }

        return instance;
    }

    public BusinessRules getBussinessRules() {
        return bussinessRules;
    }

    private WorkFlowBusiness() {

        bussinessRules = new BusinessRules();
        bussinessRules
                .name("Reglas de solicitudes")
                .startGroup()
                .name("Inicio")
                .factNotFound("GotoPorAtender")
                .finishGroup()

                .startGroup()
                .name("Por resolver")
                .factNotFound("NoHayFactPorResolver")
                .startRule()
                .name("Regla para folder para aceptar")
                .exclusive(false)
                .addLine("context.accept == 1")
                .startChildRule()
                .name("Si es aceptada ademas y tiene definido un email")
                .fact("GoToAceptarAndEmail")
                .exclusive(false)
                .addLine("context.email != null")
                .finishChildRule()

                .startChildRule()
                .name("Si es aceptada ademas y NO tiene email")
                .exclusive(false)
                .fact("GoToAceptar")
                .addLine("context.email == null")
                .finishChildRule()

                .finishRule()

                .startRule()
                .name("Regla para folder para cancelar")
                .exclusive(false)
                .fact("GotoCancelar")
                .addLine("context.accept == 2")
                .finishRule()

                .startRule()
                .name("Regla para folder para cancelar si o si")
                .exclusive(false)
                .fact("GotoCancelarSiOSi")
                .addLine("context.accept == 1")
                .finishRule()



                .finishGroup()

                .startGroup()
                .name("Por cancelar")
                .factNotFound( "GotoCancelar")
                .finishGroup()

        ;

    }
}
