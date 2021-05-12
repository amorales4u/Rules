package dev.c20.rules.engine.services.entities;

import dev.c20.rules.storage.tools.FindedStorage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class SearchRequest {
    Long count = null;
    int page = 1;
    int rowsPerPage = 10;
    String search;
    String fromPath;
    List<FindedStorage> result;
}
