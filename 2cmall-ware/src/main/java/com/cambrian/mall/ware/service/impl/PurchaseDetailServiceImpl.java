package com.cambrian.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.ware.dao.PurchaseDetailDao;
import com.cambrian.mall.ware.entity.PurchaseDetailEntity;
import com.cambrian.mall.ware.service.PurchaseDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        /*
            key:
            status:
            wareId
         */
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            // purchase_id
            // sku_id
            // id
            wrapper.and(wp -> wp.eq("id", key)
                    .or().eq("sku_id", key)
                    .or().eq("purchase_id", key));
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("status", status);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listByPurchaseId(Long purchaseId) {
        QueryWrapper<PurchaseDetailEntity> wrapper =
                new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId);
        return this.list(wrapper);
    }

}