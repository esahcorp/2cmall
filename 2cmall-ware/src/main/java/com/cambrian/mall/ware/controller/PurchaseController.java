package com.cambrian.mall.ware.controller;

import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.R;
import com.cambrian.mall.ware.entity.PurchaseEntity;
import com.cambrian.mall.ware.service.PurchaseService;
import com.cambrian.mall.ware.vo.MergeVO;
import com.cambrian.mall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 采购信息
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 18:29:12
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 完成代购单
     *
     * @param doneVO vo
     * @return R
     */
    @PostMapping("/done")
    public R finish(@RequestBody PurchaseDoneVO doneVO) {

        purchaseService.finish(doneVO);

        return R.ok();
    }

    /**
     * 领取代购单
     *
     * @param ids list of Long, 采购单 id
     * @return R
     */
    @PostMapping("/received")
    public R receivePurchase(@RequestBody List<Long> ids) {

        purchaseService.receivePurchase(ids);

        return R.ok();
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVO merge){
        purchaseService.merge(merge);

        return R.ok();
    }

    @GetMapping("/unreceive/list")
    public R listUnreceived(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceived(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @GetMapping("/list")
    // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        // 填充时间字段
        Date date = new Date();
        purchase.setCreateTime(date);
        purchase.setUpdateTime(date);
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
