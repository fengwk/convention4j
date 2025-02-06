package fun.fengwk.convention4j.springboot.starter.xxljob;

import lombok.Builder;
import lombok.Data;

/**
 * @author fengwk
 */
@Builder
@Data
public class XxlJobPropertiesSnapshot {

    private final String adminAddresses;
    private final String accessToken;
    private final String ip;
    private final Integer port;
    private final String logPath;
    private final Integer logRetentionDays;

    static XxlJobPropertiesSnapshot dump(XxlJobProperties xxlJobProperties) {
        return XxlJobPropertiesSnapshot.builder()
                .adminAddresses(xxlJobProperties.getAdminAddresses())
                .accessToken(xxlJobProperties.getAccessToken())
                .ip(xxlJobProperties.getIp())
                .port(xxlJobProperties.getPort())
                .logPath(xxlJobProperties.getLogPath())
                .logRetentionDays(xxlJobProperties.getLogRetentionDays())
                .build();
    }

}
