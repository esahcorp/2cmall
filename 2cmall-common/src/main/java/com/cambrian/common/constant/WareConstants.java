package com.cambrian.common.constant;

import lombok.Getter;

/**
 * @author kuma 2020-10-27
 */
public class WareConstants {

    public enum PurchaseStatusEnum {
        /**
         * 新建
         */
        CREATED(0, "新建"),
        /**
         * 已分配
         */
        ASSIGNED(1, "已分配"),
        /**
         * 已领取
         */
        RECEIVED(2, "已领取"),
        /**
         * 已完成
         */
        FINISHED(3, "已完成"),
        /**
         * 有异常
         */
        ERROR(4, "有异常");

        @Getter
        private final int code;

        @Getter
        private final String message;

        PurchaseStatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return this.message;
        }
    }

    public enum PurchaseDetailStatusEnum {
        /**
         * 新建
         */
        CREATED(0, "新建"),
        /**
         * 已分配
         */
        ASSIGNED(1, "已分配"),
        /**
         * 已领取
         */
        PURCHASING(2, "正在采购"),
        /**
         * 已完成
         */
        FINISHED(3, "已完成"),
        /**
         * 有异常
         */
        ERROR(4, "采购失败");

        @Getter
        private final int code;

        @Getter
        private final String message;

        PurchaseDetailStatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return this.message;
        }
    }
}
