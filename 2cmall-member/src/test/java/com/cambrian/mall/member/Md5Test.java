package com.cambrian.mall.member;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;

/**
 * MD5 是一种信息摘要算法，不可逆，抗修改
 * 但是由于其抗修改的特性，可以使用彩虹表的方式破解
 *
 * @author kuma 2021-01-04
 */
public class Md5Test {

    @Test
    public void test_mds_hex() {
        String val = DigestUtils.md5Hex("123456");
        String val1 = DigestUtils.md5Hex("123456");
        assertEquals(val, val1);
        System.out.println(val1);
        String val2 = DigestUtils.md5Hex("123456 ");
        System.out.println(val2);
        assertNotEquals(val1, val2);
    }

    @Test
    public void md5_with_salt() {
        String crypt = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(crypt);
        String crypt1 = Md5Crypt.md5Crypt("123456".getBytes());
        System.out.println(crypt1);
        assertNotEquals(crypt, crypt1);
        String salt = Md5Crypt.md5Crypt("123456".getBytes(), "$1$salt");
        System.out.println(salt);
        String salt1 = Md5Crypt.md5Crypt("123456 ".getBytes(), "$1$salt");
        System.out.println(salt1);
        assertNotEquals(salt, salt1);
    }

    @Test
    public void crypt_password_encoder() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);
        String encode2 = passwordEncoder.encode("123456");
        System.out.println(encode2);
        assertNotEquals(encode, encode2);
        assertTrue(passwordEncoder.matches("123456", encode));
        assertTrue(passwordEncoder.matches("123456", encode2));
    }
}
