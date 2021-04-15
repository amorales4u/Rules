package dev.c20.rules.engine.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.c20.rules.engine.entities.Group;
import dev.c20.rules.engine.entities.Rule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupResponse {
    String name;
    String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factNotFound;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factNotFoundMessage;

    static public GroupResponse fromGroup(Group group) {
        GroupResponse response = new GroupResponse();
        response.name = group.getName();
        response.description = group.getDescription();
        response.factNotFound = group.getFactNotFound();
        response.factNotFoundMessage = group.getFactNotFoundMessage();
        return response;
    }

}
