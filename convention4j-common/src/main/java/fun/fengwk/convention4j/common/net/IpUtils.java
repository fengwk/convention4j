package fun.fengwk.convention4j.common.net;

import java.net.*;
import java.util.Enumeration;

/**
 * IP地址工具。
 *
 * @author fengwk
 */
public class IpUtils {
    
    /**
     * 有线 enp/en/eth/em
     */
    private static final String ETH = "e";
    
    /**
     * 无线 wlp/wlan
     */
    private static final String WLAN = "w";
    
    /**
     * 无线 net
     */
    private static final String NET = "n";
    
    /**
     * bond
     */
    private static final String BOND = "b";
    
    /**
     * VPN
     */
    private static final String TAP = "tap";
    
    /**
     * 虚拟网卡
     */
    private static final String VIRTUAL = "virtual";
    
    /**
     * 缓存本地IPV4
     */
    private static volatile InetAddress LOCAL_IPV4;
    
    /**
     * 缓存本地IPV6
     */
    private static volatile InetAddress LOCAL_IPV6;

    private IpUtils() {}
    
    /**
     * 判断地址是否为ipv4。
     * 
     * @param addr
     * @return
     */
    @SuppressWarnings("restriction")
    public static boolean isIPv4(String addr) {
        if (addr == null || addr.isEmpty()) {
            return false;
        }
        try {
            InetAddress inetAddress = Inet4Address.getByName(addr);
            return inetAddress instanceof Inet4Address;
        } catch (UnknownHostException ignore) {
            return false;
        }
    }
    
    /**
     * 判断地址是否为ipv6。
     * 
     * @param addr
     * @return
     */
    @SuppressWarnings("restriction")
    public static boolean isIPv6(String addr) {
        if (addr == null || addr.isEmpty()) {
            return false;
        }
        try {
            InetAddress inetAddress = Inet6Address.getByName(addr);
            return inetAddress instanceof Inet6Address;
        } catch (UnknownHostException ignore) {
            return false;
        }
    }
    
    /**
     * 判断地址是否为ipv4。
     * 
     * @param inetAddr
     * @return
     */
    public static boolean isIPv4(InetAddress inetAddr) {
        return inetAddr != null && inetAddr.getAddress().length == 4;
    }

    /**
     * 判断地址是否为ipv6。
     * 
     * @param inetAddr
     * @return
     */
    public static boolean isIPv6(InetAddress inetAddr) {
        return inetAddr != null && inetAddr.getAddress().length == 16;
    }
    
    /**
     * 从本机网卡列表中查找首个可用ipv4地址。
     * 
     * @return
     * @throws SocketException 连接本地网卡设备发生IO异常。
     */
    public static InetAddress getLocalIPv4() throws SocketException {
        if (LOCAL_IPV4 == null) {
            synchronized (IpUtils.class) {
                if (LOCAL_IPV4 == null) {
                    LOCAL_IPV4 = getLocalIP(Inet4Address.class);
                }
            }
        }
        return LOCAL_IPV4;
    }
    
    /**
     * 从本机网卡列表中查找首个可用ipv6地址。
     * 
     * @return
     * @throws SocketException 连接本地网卡设备发生IO异常。
     */
    public static InetAddress getLocalIPv6() throws SocketException {
        if (LOCAL_IPV6 == null) {
            synchronized (IpUtils.class) {
                if (LOCAL_IPV6 == null) {
                    LOCAL_IPV6 = getLocalIP(Inet6Address.class);
                }
            }
        }
        return LOCAL_IPV6;
    }
    
    private static InetAddress getLocalIP(Class<? extends InetAddress> inetClass) throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (isAvailableLocalNetworkInterface(networkInterface)) {
                Enumeration<InetAddress> inetAddressEnum = networkInterface.getInetAddresses();
                while (inetAddressEnum.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnum.nextElement();
                    if (inetAddress.getClass() == inetClass) {
                        return inetAddress;
                    }
                }
            }
        }
        return null;
    }
    
    private static boolean isAvailableLocalNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        String displayName = networkInterface.getDisplayName().toLowerCase();
        String name = networkInterface.getName().toLowerCase();
        return networkInterface.isUp() 
                && !displayName.contains(TAP) 
                && !displayName.contains(VIRTUAL) 
                && (displayName.startsWith(ETH) || displayName.startsWith(WLAN) || displayName.startsWith(BOND)
                                || displayName.startsWith(NET) || name.startsWith(ETH) || name.startsWith(WLAN)
                                || name.startsWith(BOND) || name.startsWith(NET));
    }
    
    /**
     * 将字节数组形式的ipv4地址转为int表示的ipv4地址。
     * 
     * @param ipv4 not null
     * @return
     */
    public static int ipv4ToInt(byte[] ipv4) {
        return ((0xFF & ipv4[0]) << 24) | ((0xFF & ipv4[1]) << 16) | ((0xFF & ipv4[2]) << 8) | (0xFF & ipv4[3]);
    }
    
    /**
     * 将int表示的ipv4转为字节数组形式的ipv4地址。
     * 
     * @param ipv4
     * @return
     */
    public static byte[] intToIPv4(int ipv4) {
        return new byte[] { (byte) ((ipv4 >> 24) & 0xff), (byte) ((ipv4 >> 16) & 0xff), (byte) ((ipv4 >> 8) & 0xff), (byte) (ipv4 & 0xff) };
    }
    
}
