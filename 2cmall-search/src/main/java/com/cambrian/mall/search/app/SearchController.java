package com.cambrian.mall.search.app;

import com.cambrian.mall.search.service.MallSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kuma 2020-12-01
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService searchService;

    @GetMapping("/search.html")
    public String searchPage() {
        return "search";
    }
}
