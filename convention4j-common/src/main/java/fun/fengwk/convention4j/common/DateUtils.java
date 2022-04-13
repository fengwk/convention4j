package fun.fengwk.convention4j.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 
 * @author fengwk
 */
public final class DateUtils {
	
    /**
     * 将{@link LocalDateTime}转为{@link Instant}。
     * 
     * @param localDateTime
     * @return
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
    
    /**
     * 将{@link LocalDateTime}转换为1970-01-01T00:00:00Z开始的毫秒偏移量。
     * 
     * @param localDateTime
     * @return
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }
    
    /**
     * 将{@link LocalDateTime}转为{@link Date}。
     * 
     * @param localDateTime
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return new Date(toEpochMilli(localDateTime));
    }
    
    /**
     * 将{@link Date}转为{@link LocalDateTime}。
     * 
     * @param date
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
    }
	
}
