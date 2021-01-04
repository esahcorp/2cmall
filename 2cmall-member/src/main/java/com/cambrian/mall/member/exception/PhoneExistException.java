package com.cambrian.mall.member.exception;

/**
 * @author kuma 2021-01-04
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("Phone Number has already been register");
    }
}
