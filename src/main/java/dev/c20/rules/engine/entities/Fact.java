package dev.c20.rules.engine.entities;

import dev.c20.rules.engine.demo.facts.GroovyStringFactService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true) @Getter @Setter
public class Fact {

    private String name;
    private String description;
    private String clazzName;
    private Object instance;

    List<String> parameters = new ArrayList<>();

    public Fact addParameter(String target ) {
        parameters.add(target);
        return this;
    }

}
