package dev.c20.rules.engine.services.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class SearchRequest {
    int count = 0;
    int page = 1;
    int rowsPerPage = 10;
    String search;
    String fromPath;
    List<FindedStorage> result;
}
