package com.cambrian.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.constant.WareConstants.PurchaseDetailStatusEnum;
import com.cambrian.common.constant.WareConstants.PurchaseStatusEnum;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.ware.dao.PurchaseDao;
import com.cambrian.mall.ware.entity.PurchaseDetailEntity;
import com.cambrian.mall.ware.entity.PurchaseEntity;
import com.cambrian.mall.ware.service.PurchaseDetailService;
import com.cambrian.mall.ware.service.PurchaseService;
import com.cambrian.mall.ware.service.WareSkuService;
import com.cambrian.mall.ware.vo.MergeVO;
import com.cambrian.mall.ware.vo.PurchaseDetailDoneVO;
import com.cambrian.mall.ware.vo.PurchaseDoneVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    private final PurchaseDetailService detailService;

    private final WareSkuService wareSkuService;

    public PurchaseServiceImpl(PurchaseDetailService detailService, WareSkuService wareSkuService) {
        this.detailService = detailService;
        this.wareSkuService = wareSkuService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceived(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().in("status", 0, 1)
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void merge(MergeVO merge) {
        Long purchaseId = merge.getPurchaseId();

        //
        // 0. 自动新建采购单
        // ------------------------------------------------------------------------------
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            Date date = new Date();
            purchaseEntity.setCreateTime(date);
            purchaseEntity.setUpdateTime(date);
            purchaseEntity.setStatus(PurchaseStatusEnum.CREATED.getCode());

            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        final Long finalPurchaseId = purchaseId;

        //
        // 1. 合并采购需求：变更状态，与指定采购单关联
        // ------------------------------------------------------------------------------
        List<PurchaseDetailEntity> list = merge.getItems().stream()
                .map(detailService::getById)
                .filter(PurchaseDetailEntity::canAssign)
                .map(detail -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(detail.getId());
                    detailEntity.setPurchaseId(finalPurchaseId);
                    detailEntity.setStatus(PurchaseDetailStatusEnum.ASSIGNED.getCode());

                    return detailEntity;
                }).collect(Collectors.toList());
        detailService.updateBatchById(list);

        //
        // 2. 更新采购单修改时间
        // ------------------------------------------------------------------------------
        PurchaseEntity update = new PurchaseEntity();
        update.setId(finalPurchaseId);
        update.setUpdateTime(new Date());
        this.updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void receivePurchase(List<Long> ids) {
        //
        // 1。更新采购单状态为已领取和时间戳
        // ------------------------------------------------------------------------------
        List<PurchaseEntity> purchaseEntities = ids.stream()
                .map(this::getById)
                // 状态判定
                .filter(PurchaseEntity::canReceive)
                .map(p -> {
                    PurchaseEntity purchaseEntity = new PurchaseEntity();
                    purchaseEntity.setId(p.getId());
                    purchaseEntity.setStatus(PurchaseStatusEnum.RECEIVED.getCode());
                    purchaseEntity.setUpdateTime(new Date());
                    return purchaseEntity;
                }).collect(Collectors.toList());
        this.updateBatchById(purchaseEntities);

        //
        // 2。更新关联采购需求状态
        // ------------------------------------------------------------------------------
        List<PurchaseDetailEntity> details = purchaseEntities.stream()
                .flatMap(purchaseEntity -> detailService.listByPurchaseId(purchaseEntity.getId()).stream())
                .map(detail -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(detail.getId());
                    detailEntity.setStatus(PurchaseDetailStatusEnum.PURCHASING.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
        detailService.updateBatchById(details);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finish(PurchaseDoneVO doneVO) {
        //
        // 1. 更新采购需求状态为已完成或者失败
        // 1.5 入库存
        // 2. 根据采购需求状态更新采购单状态
        // ------------------------------------------------------------------------------
        int doneStatus = PurchaseStatusEnum.FINISHED.getCode();
        List<PurchaseDetailEntity> detailEntities = new ArrayList<>();
        for (PurchaseDetailDoneVO item : doneVO.getItems()) {
            Long skuId = item.getItemId();
            if (PurchaseDetailStatusEnum.ERROR.getCode() == item.getStatus()) {
                doneStatus = PurchaseStatusEnum.ERROR.getCode();
            } else {
                // 入库操作
                PurchaseDetailEntity detailEntity = detailService.getById(skuId);
                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }

            PurchaseDetailEntity detail = new PurchaseDetailEntity();
            detail.setId(skuId);
            detail.setStatus(item.getStatus());
            detailEntities.add(detail);
        }
        detailService.updateBatchById(detailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVO.getId());
        purchaseEntity.setStatus(doneStatus);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
}