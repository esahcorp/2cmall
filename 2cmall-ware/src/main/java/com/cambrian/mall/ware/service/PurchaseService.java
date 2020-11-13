package com.cambrian.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.mall.ware.entity.PurchaseEntity;
import com.cambrian.mall.ware.vo.MergeVO;
import com.cambrian.mall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author esahcorp
 * @email lostkite@outlook.com
 * @date 2020-09-29 18:29:12
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceived(Map<String, Object> params);

    void merge(MergeVO merge);

    void receivePurchase(List<Long> ids);

    void finish(PurchaseDoneVO doneVO);
}

