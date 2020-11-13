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
}
