package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true) @Getter @Setter
public class Fact {

    private String name;
    private String description;
    private Object instance;

}
