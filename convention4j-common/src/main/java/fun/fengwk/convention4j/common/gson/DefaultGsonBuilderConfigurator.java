package fun.fengwk.convention4j.common.gson;

import com.google.auto.service.AutoService;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.OrderedObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author fengwk
 */
@AutoService(GsonBuilderConfigurator.class)
public class DefaultGsonBuilderConfigurator implements GsonBuilderConfigurator {

    @Override
    public int getOrder() {
        return OrderedObject.HIGHEST_PRECEDENCE;
    }

    @Override
    public void config(GsonBuilder builder) {
        builder.disableHtmlEscaping();// 关闭html转义
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        builder.registerTypeAdapter(java.sql.Date.class, new SqlDateTypeAdapter());
        builder.registerTypeAdapter(Result.class, new ResultDeserializer());
        builder.registerTypeAdapter(CursorPage.class, new CursorPageDeserializer());
        builder.registerTypeAdapter(Page.class, new PageDeserializer());
    }

}
