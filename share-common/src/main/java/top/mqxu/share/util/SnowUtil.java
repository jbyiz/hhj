package top.mqxu.share.util;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {
    /**
     * 数据中⼼
     */
    private static final long DATA_CENTER_ID = 1;
    /**
     * 机器标识
     */
    private static final long WORKER_ID = 1;
    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(WORKER_ID, DATA_CENTER_ID).nextId();
    }
    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(WORKER_ID, DATA_CENTER_ID).nextIdStr();
    }
}
