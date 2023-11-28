//package fun.fengwk.convention4j.common.json.jackson;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.google.auto.service.AutoService;
//
//import java.text.SimpleDateFormat;
//
///**
// * @author fengwk
// */
//@AutoService(ObjectMapperConfigurator.class)
//public class DefaultObjectMapperConfigurator implements ObjectMapperConfigurator {
//
//    @Override
//    public void config(ObjectMapper mapper) {
//
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//            .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//
//        // 在反序列化时忽略在 json 中存在但 Java 对象不存在的属性
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        // 在序列化时日期格式默认为 yyyy-MM-dd'T'HH:mm:ss.SSSZ
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        //在序列化时自定义时间日期格式
//        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        // 在序列化时忽略值为null的属性
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//    }
//
//}
