package dev.c20.rules.engine.services.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class FindedStorage {
    String path;
    String name;
    String description;

    public FindedStorage( String path, String name, String description ) {
        this.path = path;
        this.name = name;
        this.description = description;
    }

}
