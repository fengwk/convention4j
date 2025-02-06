package fun.fengwk.convention4j.springboot.starter.xxljob;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.util.NetUtil;
import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.runtimex.RuntimeExecutionException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerGracefulShutdownLifecycle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author fengwk
 */
@Slf4j
public class RefreshableXxlJobSpringExecutor
        implements Runnable, ApplicationListener<XxlJobPropertiesChangedEvent>, ApplicationContextAware, SmartLifecycle {

    private static final int INIT = 0;
    private static final int RUNNING = 1;
    private static final int FAILED = 2;
    private static final int DESTROY = 3;

    /**
     * 去抖时间
     */
    private static final long DEBOUNCE_MS = 1000 * 5L;

    /**
     * 去抖次数
     */
    private static final int DEBOUNCE_TIMES = 3;

    private final ScheduledExecutorService reachableMonitorService = Executors.newScheduledThreadPool(1);

    private volatile ReachableNetInfo reachableNetInfo;
    private volatile int status = INIT; // 0-初始，1-运行，2-销毁

    private final String appName;
    private volatile XxlJobSpringExecutor xxlJobSpringExecutor;
    private volatile XxlJobProperties xxlJobProperties;
    private volatile ApplicationContext applicationContext;

    public RefreshableXxlJobSpringExecutor(String appName) {
        this.appName = Objects.requireNonNull(appName);
        this.reachableMonitorService.scheduleWithFixedDelay(
                this, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public synchronized void restart() throws Exception {
        if (status >= DESTROY) {
            return;
        }
        if (this.xxlJobSpringExecutor != null) {
            destroyExecutor(this.xxlJobSpringExecutor);
            this.xxlJobSpringExecutor = null;
        }
        try {
            XxlJobSpringExecutor xxlJobSpringExecutor = buildXxlJobSpringExecutor();
            startExecutor(xxlJobSpringExecutor);
            this.xxlJobSpringExecutor = xxlJobSpringExecutor;
            this.status = RUNNING;
        } catch (Exception ex) {
            this.status = FAILED;
            throw ex;
        }
    }

    private synchronized void destroy() {
        this.status = DESTROY;
        if (this.xxlJobSpringExecutor != null) {
            destroyExecutor(this.xxlJobSpringExecutor);
            this.xxlJobSpringExecutor = null;
        }
        this.reachableMonitorService.shutdown();
    }

    private void startExecutor(XxlJobSpringExecutor xxlJobSpringExecutor) {
        xxlJobSpringExecutor.setApplicationContext(applicationContext);
        xxlJobSpringExecutor.afterSingletonsInstantiated();
    }

    private void destroyExecutor(XxlJobSpringExecutor xxlJobSpringExecutor) {
        xxlJobSpringExecutor.destroy();
    }

    @Override
    public synchronized void onApplicationEvent(XxlJobPropertiesChangedEvent event) {
        this.xxlJobProperties = event.getXxlJobProperties();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run() {
        if (status == FAILED) {
            try {
                log.info("xxl job retry restarting...");
                restart();
                log.info("xxl job retry restart successfully");
            } catch (Exception ex) {
                log.error("xxl job retry restart failed", ex);
            }
        } else if (status == RUNNING) {
            ReachableNetInfo reachableNetInfo = this.reachableNetInfo;
            if (reachableNetInfo != null) {
                try {
                    if (!isReachable(reachableNetInfo.getXxlJobAdminAddress(), reachableNetInfo.getNetworkInterface())) {
                        // 去抖，避免是网络快速抖动导致的问题
                        for (int c = 0; c < DEBOUNCE_TIMES; c++) {
                            if (isReachable(reachableNetInfo.getXxlJobAdminAddress(), reachableNetInfo.getNetworkInterface())) {
                                return;
                            }
                            try {
                                Thread.sleep(DEBOUNCE_MS);
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        // 重启执行器
                        try {
                            log.info("xxl job restarting...");
                            restart();
                            log.info("xxl job restart successfully");
                        } catch (Exception ex) {
                            log.error("xxl job restart failed", ex);
                        }
                    }
                } catch (UnknownHostException ex) {
                    log.error("error occurred while checking network reachability", ex);
                }
            }
        }
    }

    private XxlJobSpringExecutor buildXxlJobSpringExecutor()
            throws SocketException, UnknownHostException, URISyntaxException {

        log.info("{} init", XxlJobSpringExecutor.class.getSimpleName());

        if (StringUtils.isBlank(xxlJobProperties.getAdminAddresses())) {
            throw new IllegalStateException("xxl job executor admin address must not be blank");
        }

        int port = resolvePort(xxlJobProperties.getPort());

        URI adminUri = URI.create(xxlJobProperties.getAdminAddresses());
        String adminHost = adminUri.getHost();
        this.reachableNetInfo = detectAvailableIp(adminHost);
        if (reachableNetInfo == null) {
            throw new IllegalStateException("xxl job executor can not found available ip");
        }

        URI addressUri = new URI("http", null, reachableNetInfo.getInetAddress().getHostAddress(), port,
                null, null, null);
        String address = addressUri.toASCIIString();

        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(xxlJobProperties.getIp());
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());

        return xxlJobSpringExecutor;
    }

    private int resolvePort(int port) {
        return port > 0 ? port : NetUtil.findAvailablePort(9999);
    }

    public boolean isReachable(InetAddress hostAddress, NetworkInterface networkInterface) throws UnknownHostException {
        try {
            return hostAddress.isReachable(networkInterface, 0, 5000);
        } catch (IOException ex) {
            log.debug("isReachable error", ex);
            return false;
        }
    }

    private InetAddress getInetAddress(NetworkInterface networkInterface, Class<? extends InetAddress> inetClass) {
        Enumeration<InetAddress> inetAddressEnum = networkInterface.getInetAddresses();
        while (inetAddressEnum.hasMoreElements()) {
            InetAddress inetAddress = inetAddressEnum.nextElement();
            if (inetAddress.getClass() == inetClass) {
                return inetAddress;
            }
        }
        return null;
    }

    private ReachableNetInfo detectAvailableIp(String adminHost) throws UnknownHostException, SocketException {
        InetAddress xxlJobAdminAddress = InetAddress.getByName(adminHost);
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isUp() && isReachable(xxlJobAdminAddress, networkInterface)) {
                InetAddress inetAddress = getInetAddress(networkInterface, Inet4Address.class);
                if (inetAddress != null) {
                    return new ReachableNetInfo(xxlJobAdminAddress, networkInterface, inetAddress);
                }
                inetAddress = getInetAddress(networkInterface, Inet6Address.class);
                if (inetAddress != null) {
                    return new ReachableNetInfo(xxlJobAdminAddress, networkInterface, inetAddress);
                }
            }
        }
        return null;
    }

    @Override
    public void start() {
        // 在应用初始化完成后再启动以便解决循环依赖问题
        try {
            restart();
        } catch (Exception ex) {
            throw new RuntimeExecutionException(ex);
        }
    }

    @Override
    public int getPhase() {
        // 与WebServer同一批次下线
        return WebServerGracefulShutdownLifecycle.SMART_LIFECYCLE_PHASE;
    }

    @Override
    public void stop() {
        destroy();
    }

    @Override
    public boolean isRunning() {
        return status == RUNNING;
    }

    @Data
    static class ReachableNetInfo {

        private final InetAddress xxlJobAdminAddress;
        private final NetworkInterface networkInterface;
        private final InetAddress inetAddress;

    }

}
