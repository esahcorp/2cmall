package com.cambrian.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 商品检索页面搜索条件封装
 *
 * @author kuma 2020-12-02
 */
@Data
public class SearchParam {

    public static final String VALUE_TYPE_DELIMITER = "_";
    public static final String VALUE_LIST_DELIMITER = ":";

    public static final String DEFAULT_HIGHLIGHT_PRE_TAG = "<b style='color:red'>";
    public static final String DEFAULT_HIGHLIGHT_POST_TAG = "</b>";

    /**
     * 全文匹配关键字
     */
    private String keyword;

    /**
     * 第三级分类 id
     */
    private Long catalog3Id;

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;

    private Integer hasStock;

    private String skuPrice;

    private List<Long> brandId;
    /**
     * such as: attrs=1_其它:安卓&attrs=2_5寸:6寸
     * attrs={id}_{value}[:{value}][&attrs=**]
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;
    /**
     * 页面大小
     */
    private Integer pageSize = 20;

    private String highlightPreTag = DEFAULT_HIGHLIGHT_PRE_TAG;
    private String highlightPostTag = DEFAULT_HIGHLIGHT_POST_TAG;
}
