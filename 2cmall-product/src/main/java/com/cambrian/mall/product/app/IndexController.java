package com.cambrian.mall.product.app;

import com.cambrian.mall.product.entity.CategoryEntity;
import com.cambrian.mall.product.service.CategoryService;
import com.cambrian.mall.product.vo.Catalog2VO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author kuma 2020-11-24
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    // 视图解析器
    // ThymeleafProperties.DEFAULT_PREFIX = "classpath:/templates/"
    // ThymeleafProperties.DEFAULT_SUFFIX = ".html"

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        List<CategoryEntity> categories = categoryService.listRootCategories();
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/index/json/catalog.json")
    @ResponseBody
    public Map<String, List<Catalog2VO>> getCatalogJson() {
        return categoryService.listCatalogJsonModel();
    }
}
