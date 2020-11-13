package com.cambrian.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 完成采购单 vo
 *
 * @author kuma
 */
@Data
public class PurchaseDoneVO{
	@NotNull
	private Long id;
	private List<PurchaseDetailDoneVO> items;
}