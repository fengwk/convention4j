package fun.fengwk.convention4j.api.gson;

import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.api.result.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author fengwk
 */
public class DefaultGsonBuilderConfigurator implements GsonBuilderConfigurator {

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void config(GsonBuilder builder) {
        builder.disableHtmlEscaping();// 关闭html转义
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        builder.registerTypeAdapter(java.sql.Date.class, new SqlDateTypeAdapter());
        builder.registerTypeAdapter(Result.class, new ResultTypeAdapter());
    }

}
