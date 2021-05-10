package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.demo.facts.GroovyStringFactService;
import dev.c20.workflow.commons.tools.StringUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;


@Accessors( chain = true)
@Getter
@Setter
public class Fact {

    private String name;
    private String description;
    private String path;
    private String clazzName;
    private Object instance;
    Map<String,String> properties = new LinkedHashMap<>();

    List<String> parameters = new ArrayList<>();

    public Fact addProperty(String name, String value ) {
        properties.put(name,value);
        return this;
    }

    public Fact addParameter(String target ) {
        parameters.add(target);
        return this;
    }

    public String getParametersAsList() {
        return StringUtils.listAsString(parameters,"\n");
    }

}
