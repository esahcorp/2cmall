package com.cambrian.mall.auth.utils;

/**
 * 雪花算法生成分布式 id

 * <p>Twitter_Snowflake</p>
 * SnowFlake的结构：
 * <br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * <br>
 * 1 位标识，由于 long 基本类型在 Java 中是带符号的，最高位是符号位，正数是 0，负数是 1，所以 id 一般是正数，最高位是 0<br>
 * 41 位时间截(毫秒级)，注意，41 位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截),
 * 这里的的开始时间截，一般是我们的 id生成器开始使用的时间，由我们程序来指定的(如 START_TIMESTAMP)。
 * 41 位的时间截，可以使用 69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10 位的数据机器位，可以部署在 1024 个节点，包括 5 位 dataCenterId 和 5 位 machineId <br>
 * 12 位序列，毫秒内的计数，12 位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生 4096 个ID序号<br>
 *
 * @author kuma
 */
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private static final long START_TIMESTAMP = 1565600875255L;

    //===============================================================================
    // 每一部分占用的位数
    //===============================================================================
    /**
     * 序列号占用的位数
     */
    private static final long SEQUENCE_BIT = 12;
    /**
     * 机器标识占用的位数
     */
    private static final long MACHINE_BIT = 5;
    /**
     * 数据中心占用的位数
     */
    private static final long DATA_CENTER_BIT = 5;

    //===============================================================================
    //  每一部分的最大值
    //===============================================================================
    private static final long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    //===============================================================================
    //  每一部分向左的位移
    //===============================================================================
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    /**
     * 数据中心
     */
    private final long dataCenterId;
    /**
     * 机器标识
     */
    private final long machineId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 上一次时间戳
     */
    private long lastTimestamp = -1L;

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 返回下一个 id
     *
     * @return long
     */
    public synchronized long nextId() {
        long currentTimeStamp = currentTimeStamp();
        //===============================================================================
        //  时钟回拨
        //===============================================================================
        if (currentTimeStamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("发生时钟回拨. 在 %d milliseconds 内暂停生成 id ",
                    this.lastTimestamp - currentTimeStamp));
        }
        //===============================================================================
        //  1. 相同时间内，序列号自增
        //  2. 不同时间内，序列号置为 0
        //===============================================================================
        if (currentTimeStamp == this.lastTimestamp) {
            // 001 & 111 = 001  (111 + 1) & 111 = 000
            this.sequence = (sequence + 1) & MAX_SEQUENCE;
            if (this.sequence == 0L) {
                currentTimeStamp = nextAvailableTimestamp();
            }
        } else {
            this.sequence = 0L;
        }
        this.lastTimestamp = currentTimeStamp;
        //===============================================================================
        // 位移之后或运算，实现拼接
        //
        //  时间戳部分
        //  | 数据中心部分
        //  | 机器标识部分
        //  | 序列号部分
        //===============================================================================
        return (currentTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | this.dataCenterId << DATA_CENTER_LEFT
                | this.machineId << MACHINE_LEFT
                | this.sequence;
    }

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    private long currentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取下一个可用时间戳
     *
     * @return long
     */
    private long nextAvailableTimestamp() {
        long timeStamp = currentTimeStamp();
        // 预防时钟回拨
        while (timeStamp <= this.lastTimestamp) {
            timeStamp = currentTimeStamp();
        }
        return timeStamp;
    }
}
