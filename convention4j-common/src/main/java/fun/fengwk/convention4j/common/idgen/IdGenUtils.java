package fun.fengwk.convention4j.common.idgen;

import java.util.UUID;

/**
 * @author fengwk
 */
public class IdGenUtils {

    private static final char TRIM = '-';

    /**
     * 生成UUID（无分隔符减少存储占用）
     *
     * @return UUID
     */
    public static String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uuid.length(); i++) {
            if (uuid.charAt(i) != TRIM) {
                sb.append(uuid.charAt(i));
            }
        }
        return sb.toString();
    }

}
