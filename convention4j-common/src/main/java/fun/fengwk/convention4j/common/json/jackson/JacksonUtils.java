package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;

/**
 * @author fengwk
 */
@Slf4j
public class JacksonUtils {

    private JacksonUtils() {}

    public static <T> T readValue(File src, Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(File src, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(File src, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(URL src, Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(URL src, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(URL src, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(String content, Class<T> valueType)
    {
        if (content == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(content, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef)
    {
        if (content == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(content, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(String content, JavaType valueType)
    {
        if (content == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(content, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(Reader src, Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(Reader src, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(Reader src, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(InputStream src, Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(InputStream src, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(InputStream src, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, int offset, int len,
                           Class<T> valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, offset, len, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, offset, len, valueTypeRef);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(byte[] src, int offset, int len, JavaType valueType)
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, offset, len, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(DataInput src, Class<T> valueType) throws IOException
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static <T> T readValue(DataInput src, JavaType valueType) throws IOException
    {
        if (src == null) {
            return null;
        }
        try {
            return ObjectMapperHolder.getInstance().readValue(src, valueType);
        } catch (Exception ex) {
            log.error("readValue error", ex);
            return null;
        }
    }

    public static void writeValue(File resultFile, Object value)
        throws IOException, StreamWriteException, DatabindException
    {
        ObjectMapperHolder.getInstance().writeValue(resultFile, value);
    }

    public static void writeValue(OutputStream out, Object value)
        throws IOException, StreamWriteException, DatabindException
    {
        ObjectMapperHolder.getInstance().writeValue(out, value);
    }

    public static void writeValue(DataOutput out, Object value) throws IOException
    {
        ObjectMapperHolder.getInstance().writeValue(out, value);
    }

    public static void writeValue(Writer w, Object value)
        throws IOException, StreamWriteException, DatabindException
    {
        ObjectMapperHolder.getInstance().writeValue(w, value);
    }

    public static String writeValueAsString(Object value)
    {
        try {
            return ObjectMapperHolder.getInstance().writeValueAsString(value);
        } catch (Exception ex) {
            log.error("writeValueAsString error", ex);
            return null;
        }
    }

    public static byte[] writeValueAsBytes(Object value)
    {
        try {
            return ObjectMapperHolder.getInstance().writeValueAsBytes(value);
        } catch (Exception ex) {
            log.error("writeValueAsBytes error", ex);
            return new byte[0];
        }
    }

}
