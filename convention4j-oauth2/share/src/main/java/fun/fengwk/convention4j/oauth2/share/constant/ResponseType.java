package fun.fengwk.convention4j.oauth2.share.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 响应类型
 * @author fengwk
 */
@Getter
@AllArgsConstructor
public enum ResponseType {

    CODE("code"), TOKEN("token");

    private final String code;

    public static ResponseType of(String code) {
        for (ResponseType type : values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        return null;
    }

}
