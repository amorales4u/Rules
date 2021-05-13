package dev.c20.rules.search;

import dev.c20.rules.engine.services.RuleService;
import dev.c20.rules.engine.services.entities.SearchRequest;
import dev.c20.rules.search.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SearchRestController {

    @Autowired
    RuleService ruleService;
    
    @Autowired
    SearchService searchService;

    @GetMapping("/version")
    public String version() {
        return "2021-04-13 18:50";
    }

    @PostMapping("/stg/search/")
    public SearchRequest search(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.search(request);
    }

    @PostMapping("/stg/search-ids/")
    public SearchRequest searchIds(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.searchIds(request);
    }





}
