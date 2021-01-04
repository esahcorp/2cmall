package com.cambrian.mall.auth.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * 序列号相关工具类
 *
 * @author kuma
 */
public final class SequenceUtils {

    private static final SnowFlake SNOW_FLAKE;

    static {
        SNOW_FLAKE = new SnowFlake(dataCenterId(), machineId());
    }

    private SequenceUtils() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class!");
    }

    /**
     * 返回随机六位整数，用来生成验证码
     *
     * @return String of six digits, 取值集合 [000_000, 999_999)
     */
    public static String getSixDigits() {
        return StringUtils.leftPad(new Random().nextInt(1_000_000) + "", 6, "0");
    }

    /**
     * 基于❄️算法的分布式 id
     *
     * @param prefix String 前缀
     * @return prefix + 18 位 id
     */
    public static String getDistributedId(String prefix) {
        return prefix + SNOW_FLAKE.nextId();
    }

    private static Long machineId() {
        try {
            // IP
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int[] unicodeArray = toCodePoints(hostAddress);
            int sums = 0;
            for (int b : unicodeArray) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

    private static Long dataCenterId() {
        try {
            // 域名（such as: xxx's PC / localhost）
            int[] unicode = toCodePoints(InetAddress.getLocalHost().getHostName());
            int sums = 0;
            for (int i : unicode) {
                sums += i;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }

    /**
     * @see org.apache.commons.lang3.StringUtils sicne version 3.9
     * @param str
     * @return
     */
    private static int[] toCodePoints(final CharSequence str) {
        if (str == null) {
            return new int[0];
        }
        if (str.length() == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }

        final String s = str.toString();
        final int[] result = new int[s.codePointCount(0, s.length())];
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            result[i] = s.codePointAt(index);
            index += Character.charCount(result[i]);
        }
        return result;
    }
}
