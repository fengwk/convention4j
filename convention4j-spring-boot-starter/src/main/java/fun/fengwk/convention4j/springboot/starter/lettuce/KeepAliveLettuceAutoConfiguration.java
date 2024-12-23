package fun.fengwk.convention4j.springboot.starter.lettuce;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.NettyCustomizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.epoll.EpollChannelOption;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author fengwk
 * @see <a href="https://github.com/lettuce-io/lettuce-core/issues/1428">Lettuce cannot recover from connection problems</a>
 */
@ConditionalOnClass(RedisClient.class)
@AutoConfiguration
public class KeepAliveLettuceAutoConfiguration {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceCustomizer() {

        return builder -> {
            ClientResources clientResources = ClientResources.builder().nettyCustomizer(new NettyCustomizer() {
                @Override
                public void afterBootstrapInitialized(Bootstrap bootstrap) {
                    bootstrap.option(EpollChannelOption.TCP_KEEPIDLE, 15);
                    bootstrap.option(EpollChannelOption.TCP_KEEPINTVL, 5);
                    bootstrap.option(EpollChannelOption.TCP_KEEPCNT, 3);
                    // Socket Timeout (milliseconds)
                    bootstrap.option(EpollChannelOption.TCP_USER_TIMEOUT, 60000);
                }
            }).build();

            // Enabled keep alive
            SocketOptions socketOptions = SocketOptions.builder()
                .keepAlive(true).build();
            ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions).build();

            builder.clientResources(clientResources).clientOptions(clientOptions);
        };
    }

}
