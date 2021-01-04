package com.cambrian.mall.member.exception;

/**
 * @author kuma 2021-01-04
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("Name has already exist");
    }
}
