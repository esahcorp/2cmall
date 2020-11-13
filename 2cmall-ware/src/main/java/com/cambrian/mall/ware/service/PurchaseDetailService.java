package com.cambrian.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 18:29:12
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<PurchaseDetailEntity> listByPurchaseId(Long purchaseId);
}

