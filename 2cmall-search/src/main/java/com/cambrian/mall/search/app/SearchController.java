package com.cambrian.mall.search.app;

import com.cambrian.mall.search.service.MallSearchService;
import com.cambrian.mall.search.vo.SearchParam;
import com.cambrian.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author kuma 2020-12-01
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService searchService;

    @GetMapping("/search.html")
    public String searchPage(SearchParam searchParam, Model model) {

        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result", result);

        return "search";
    }
}
