package com.cambrian.common.constant;

import lombok.Getter;

/**
 * @author kuma 2020-10-27
 */
public class ProductConstants {

    public enum AttrTypeEnum {
        /**
         * 销售属性
         */
        ATTR_TYPE_SALE(0, "sale"),
        /**
         * 基本属性
         */
        ATTR_TYPE_BASE(1, "base");

        @Getter
        private final int code;

        @Getter
        private final String message;

        AttrTypeEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public static boolean isBase(int code) {
            return ATTR_TYPE_BASE.code == code;
        }

        public static boolean isBase(String  message) {
            return ATTR_TYPE_BASE.message.equals(message);
        }

        public static AttrTypeEnum messageOf(String message) {
            for (AttrTypeEnum attrType : values()) {
                if (attrType.getMessage().equals(message)) {
                    return attrType;
                }
            }
            throw new RuntimeException("Illegal message of AttrTypeEnum");
        }

        @Override
        public String toString() {
            return this.message;
        }
    }

    public enum SpuPublishStatus {
        /**
         * 新建
         */
        NEW(0),
        /**
         * 上架
         */
        UP(1),
        /**
         * 下架
         */
        DOWN(2);

        private final int code;

        SpuPublishStatus(int code) {
            this.code = code;
        }


        public int getCode() {
            return code;
        }
    }

    public static final String SERVICE_CODE = "PMS";

    public static final class CacheKey {
        private CacheKey() {
            throw new UnsupportedOperationException("Until class");
        }

        public static final String CATALOG_JSON = SERVICE_CODE + "-catalogJson";
    }

    public static final class RedisLockKey {
        private RedisLockKey() {
            throw new UnsupportedOperationException("Until class");
        }

        public static final String LOCK_CATALOG_JSON = "lock-" + CacheKey.CATALOG_JSON;
    }
}
