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
        ATTR_TYPE_SALE("sale"),
        /**
         * 基本属性
         */
        ATTR_TYPE_BASE("base");

        @Getter
        private final String message;

        AttrTypeEnum(String message) {
            this.message = message;
        }

        public static boolean isBase(int code) {
            return ATTR_TYPE_BASE.ordinal() == code;
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
