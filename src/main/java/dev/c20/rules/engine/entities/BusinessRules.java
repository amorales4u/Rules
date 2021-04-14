package dev.c20.rules.engine.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BusinessRules implements Serializable {

    String name;
    List<Group> rulesGroups = new ArrayList<>();

    public BusinessRules name(String name ) {
        this.name = name;
        return this;
    }

    public Group startGroup() {
        Group rulesGroup = new Group();
        rulesGroup.parent = this;
        return rulesGroup;
    }

    public Group find(String name) {

        for( Group rulesGroup: rulesGroups ) {
            if( rulesGroup.getName().equals(name) ) {
                return rulesGroup;
            }
        }

        return null;
    }


}
