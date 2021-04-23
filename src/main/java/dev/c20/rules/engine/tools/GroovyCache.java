package dev.c20.rules.engine.tools;

import groovy.lang.GroovyObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroovyCache {

    int hashSource;
    GroovyObject instance;
}
