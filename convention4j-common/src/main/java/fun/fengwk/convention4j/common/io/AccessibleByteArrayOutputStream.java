package fun.fengwk.convention4j.common.io;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * @author fengwk
 */
public class AccessibleByteArrayOutputStream extends ByteArrayOutputStream {

    /**
     * 获取指定位置的字节
     *
     * @param index 索引
     * @return 字节
     */
    public byte get(int index) {
        Objects.checkIndex(index, size());
        return buf[index];
    }

}
