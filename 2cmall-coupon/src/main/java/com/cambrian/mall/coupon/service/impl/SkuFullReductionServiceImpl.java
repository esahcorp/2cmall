package com.cambrian.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cambrian.common.to.MemberPriceItem;
import com.cambrian.common.to.SkuReductionTO;
import com.cambrian.common.utils.PageUtils;
import com.cambrian.common.utils.Query;
import com.cambrian.mall.coupon.dao.SkuFullReductionDao;
import com.cambrian.mall.coupon.entity.MemberPriceEntity;
import com.cambrian.mall.coupon.entity.SkuFullReductionEntity;
import com.cambrian.mall.coupon.entity.SkuLadderEntity;
import com.cambrian.mall.coupon.service.MemberPriceService;
import com.cambrian.mall.coupon.service.SkuFullReductionService;
import com.cambrian.mall.coupon.service.SkuLadderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    private final SkuLadderService skuLadderService;
    private final MemberPriceService memberPriceService;

    public SkuFullReductionServiceImpl(SkuLadderService skuLadderService, MemberPriceService memberPriceService) {
        this.skuLadderService = skuLadderService;
        this.memberPriceService = memberPriceService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuReductionInfo(SkuReductionTO skuReductionTo) {
        Long skuId = skuReductionTo.getSkuId();

        // 5.4 sku 的优惠满减信息 sms_sku_full_reduction  sms_sku_ladder   sms_member_price
        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            // 仅保存有意义的数据
            SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
            reductionEntity.setSkuId(skuId);
            reductionEntity.setReducePrice(skuReductionTo.getReducePrice());
            reductionEntity.setFullPrice(skuReductionTo.getFullPrice());
            this.save(reductionEntity);
        }

        if (skuReductionTo.getFullCount() > 0) {
            // 仅保存有意义的数据
            SkuLadderEntity ladderEntity = new SkuLadderEntity();
            ladderEntity.setSkuId(skuId);
            ladderEntity.setFullCount(skuReductionTo.getFullCount());
            ladderEntity.setDiscount(skuReductionTo.getDiscount());
            ladderEntity.setAddOther(skuReductionTo.getCountStatus());

            skuLadderService.save(ladderEntity);
        }

        List<MemberPriceItem> memberPriceList = skuReductionTo.getMemberPrice();
        if (!CollectionUtils.isEmpty(memberPriceList)) {
            List<MemberPriceEntity> memberPriceEntities = memberPriceList.stream()
                    // 保存有意义的数据
                    .filter(item -> item.getPrice().compareTo(BigDecimal.ZERO) > 0)
                    .map(mp -> {
                        MemberPriceEntity memberPrice = new MemberPriceEntity();
                        memberPrice.setSkuId(skuId);
                        memberPrice.setMemberLevelId(mp.getId());
                        memberPrice.setMemberLevelName(mp.getName());
                        memberPrice.setMemberPrice(mp.getPrice());
                        memberPrice.setAddOther(1);

                        return memberPrice;
                    }).collect(Collectors.toList());
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }

}