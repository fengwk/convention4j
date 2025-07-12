package fun.fengwk.convention4j.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * 
 * @author fengwk
 */
public class DateUtils {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private DateUtils() {}

    /**
     * UTC ZoneId
     *
     * @return UTC ZoneId
     */
    public static ZoneId zoneIdUTC() {
        return ZONE_ID_UTC;
    }

    /**
     * 将{@link LocalDateTime}转为{@link Instant}。
     * 
     * @param localDateTime
     * @return
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
    
    /**
     * 将{@link LocalDateTime}转换为1970-01-01T00:00:00Z开始的毫秒偏移量。
     * 
     * @param localDateTime
     * @return
     */
    public static Long toEpochMilli(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        Instant instant = toInstant(localDateTime);
        assert instant != null;
        return instant.toEpochMilli();
    }
    
    /**
     * 将{@link LocalDateTime}转为{@link Date}。
     * 
     * @param localDateTime
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        Long epochMilli = toEpochMilli(localDateTime);
        assert epochMilli != null;
        return new Date(epochMilli);
    }
    
    /**
     * 将{@link Date}转为{@link LocalDateTime}。
     * 
     * @param date
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }

    /**
     * 将毫秒时间戳转为{@link LocalDateTime}。
     *
     * @param epochMilli
     * @return
     */
    public static LocalDateTime toLocalDateTime(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        }
        return toLocalDateTime(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * 将{@link Instant}转为{@link LocalDateTime}。
     *
     * @param instant
     * @return
     */
    public static LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 讲sourceTime从时区sourceZoneId转换为targetZoneId
     *
     * @param sourceTime 来源时间
     * @param sourceZoneId 来源时区
     * @param targetZoneId 目标时区
     * @return 目标时间
     */
    public static LocalDateTime convertTimeZone(LocalDateTime sourceTime, ZoneId sourceZoneId, ZoneId targetZoneId) {
        if (sourceTime == null) {
            return null;
        }
        if (Objects.equals(sourceZoneId, targetZoneId)) {
            return sourceTime;
        }
        return sourceTime.atZone(sourceZoneId)
            .withZoneSameInstant(targetZoneId)
            .toLocalDateTime();
    }

}
