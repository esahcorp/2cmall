package com.cambrian.mall.ware.vo;

import lombok.Data;

/**
 * 完成采购需求 vo
 *
 * @author kuma
 */
@Data
public class PurchaseDetailDoneVO {

	private Long itemId;
	private String reason;
	private Integer status;
}
