package fun.fengwk.convention4j.api.gson;

import com.google.common.collect.*;
import com.google.gson.GsonBuilder;
import fun.fengwk.convention4j.api.result.Result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * @author fengwk
 */
public class DefaultGsonBuilderFactory {

    private DefaultGsonBuilderFactory() {}
    
    public static GsonBuilder create() {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();// 关闭html转义
        builder.registerTypeAdapter(Long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(long.class, new LongTypeAdapter());
        builder.registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());
        builder.registerTypeAdapter(java.sql.Date.class, new SqlDateTypeAdapter());
        builder.registerTypeAdapter(Result.class, new ResultTypeAdapter());
        builder.registerTypeAdapter(ImmutableCollection.class, new ImmutableListDeserializer());
        builder.registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer());
        builder.registerTypeAdapter(ImmutableSet.class, new ImmutableSetJsonDeserializer());
        builder.registerTypeAdapter(ImmutableSortedSet.class, new ImmutableSetJsonDeserializer());
        builder.registerTypeAdapter(ImmutableMap.class, new ImmutableMapJsonDeserializer());
        builder.registerTypeAdapter(ImmutableSortedMap.class, new ImmutableMapJsonDeserializer());
        return builder;
    }
    
}
