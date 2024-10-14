package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Constant {

    public static final String CURRENT_USER = "current_user";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }

    public interface Cart{
        int CHECKED = 1; // selected in cart
        int UNCHECKED = 0; // not select in cart

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0; // general customer
        int ROLE_ADMIN = 1; // admin
    }

    public enum ProductStatusEnum{
        ON_SALE(1, "ONLINE");


        private String value;
        private int code;
        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum{
        CANCELED(0, "CANCELED"),
        NO_PAY(10, "NO_PAY"),
        PAID(20, "PAID"),
        SHIPPED(40, "SHIPPED"),
        ORDER_SUCCESS(50, "ORDER_SUCCESS"),
        ORDER_CLOSE(60, "ORDER_CLOSE");


        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("Didn't find corresponding payment type");
        }

    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum {
        ALIPAY(1, "alipay");

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1, "online_pay");

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                 if(paymentTypeEnum.getCode() == code){
                     return paymentTypeEnum;
                 }
            }
            throw new RuntimeException("Didn't find corresponding payment type");
        }
    }




}
