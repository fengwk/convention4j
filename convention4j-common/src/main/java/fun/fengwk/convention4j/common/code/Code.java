package fun.fengwk.convention4j.common.code;

import java.io.Serializable;

/**
 * 状态码用于表示程序的运行时状态。
 *
 * @author fengwk
 */
public interface Code extends Serializable {

    /**
     * 成功状态码编码。
     */
    String SUCCESS_CODE = "0";

    /**
     * 获取编码。
     * @return 当前状态码的编码
     */
    String getCode();

    /**
     * 成功状态码。
     */
    Code SUCCESS = new Code() {

        private static final long serialVersionUID = 1L;

        @Override
        public String getCode() {
            return SUCCESS_CODE;
        }

    };

}
