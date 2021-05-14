package dev.c20.rules.search;

import dev.c20.rules.engine.services.RuleService;
import dev.c20.rules.search.requestresponses.SearchRequest;
import dev.c20.rules.search.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping( path = "/storage")
public class SearchRestController {

    @Autowired
    RuleService ruleService;
    
    @Autowired
    SearchService searchService;

    @GetMapping("/version")
    public String version() {
        return "2021-04-13 18:50";
    }

    @PostMapping("/search/")
    public SearchRequest search(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.search(request);
    }

    @PostMapping("/search-ids/")
    public SearchRequest searchIds(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.searchIds(request);
    }

    @PostMapping("/search-index/")
    public SearchRequest searchIndex(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.searchIndex(request);
    }

    @PostMapping("/search-index-data/")
    public SearchRequest searchIndexWithData(@RequestBody SearchRequest request, HttpServletRequest httpRequest) {
        return searchService.searchIndexWithData(request);
    }





}
