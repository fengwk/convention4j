package fun.fengwk.convention4j.common;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 
 * @author fengwk
 */
public class DateUtils {

    private DateUtils() {}

    /**
     * 将{@link LocalDateTime}转为{@link Instant}。
     * 
     * @param localDateTime
     * @return
     */
    @Nullable
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
    @Nullable
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
    @Nullable
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
    @Nullable
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
	
}
