package com.cambrian.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.common.utils.R;
import com.cambrian.mall.ware.dao.WareSkuDao;
import com.cambrian.mall.ware.entity.WareSkuEntity;
import com.cambrian.mall.ware.feign.ProductFeignService;
import com.cambrian.mall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        /*
            wareId: 123,//仓库id
            skuId: 123//商品id
         */
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 检查是否存在库存记录
        List<WareSkuEntity> exists = this.list(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        // 已存在记录则更新，否则新建
        if (!CollectionUtils.isEmpty(exists)) {
            wareSkuDao.updateStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity create = new WareSkuEntity();
            create.setSkuId(skuId);
            create.setWareId(wareId);
            create.setStock(skuNum);
            create.setStockLocked(0);
            try {
                R r = productFeignService.info(skuId);
                if (r.isSuccess()) {
                    Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                    create.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                // do nothing
                log.warn("Fetch SKU[{}] name by feign fail", skuId);
            }
            wareSkuDao.insert(create);
        }
    }

}