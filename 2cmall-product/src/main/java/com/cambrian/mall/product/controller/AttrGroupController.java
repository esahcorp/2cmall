package com.cambrian.mall.product.controller;

import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.R;
import com.cambrian.mall.product.entity.AttrEntity;
import com.cambrian.mall.product.entity.AttrGroupEntity;
import com.cambrian.mall.product.service.AttrAttrgroupRelationService;
import com.cambrian.mall.product.service.AttrGroupService;
import com.cambrian.mall.product.service.AttrService;
import com.cambrian.mall.product.service.CategoryService;
import com.cambrian.mall.product.vo.AttrGroupWithAttrVO;
import com.cambrian.mall.product.vo.AttrRelationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-20 04:04:32
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    @GetMapping("/{catelogId}/withattr")
    public R listGroupWithAttrViaCategory(@PathVariable("catelogId") Long catalogId) {
        List<AttrGroupWithAttrVO> vos = attrGroupService.listGroupWithAttrByCatalogId(catalogId);
        return R.ok().put("data", vos);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R listRelationAttrByGroup(@PathVariable("attrgroupId") Long attrGroupId) {
        List<AttrEntity> attrList = attrService.listRelationAttrByGroup(attrGroupId);
        return R.ok().put("data", attrList);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R listNoRelatedAttr(@PathVariable("attrgroupId") Long attrGroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.listNotRelatedAttrViaCategory(attrGroupId, params);
        return R.ok().put("page", page);
    }

    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrRelationVO> vos) {
        relationService.saveRelations(vos);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R listByCategory(@PathVariable Long categoryId, @RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(categoryId, params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] catelogPath = categoryService.findCategoryPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody List<AttrRelationVO> relations) {
        attrGroupService.removeRelationBatch(relations);
        return R.ok();
    }

}
