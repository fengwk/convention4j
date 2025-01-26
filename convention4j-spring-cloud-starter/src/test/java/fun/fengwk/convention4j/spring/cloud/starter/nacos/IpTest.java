package fun.fengwk.convention4j.spring.cloud.starter.nacos;

import java.net.*;
import java.util.Enumeration;

/**
 * @author fengwk
 */
public class IpTest {

//    @Test
    public void test() throws SocketException {
        NetworkInterface netInterface = NetworkInterface.getByName("tailscale0");
        if (netInterface != null) {
            Enumeration<InetAddress> inetAddress = netInterface.getInetAddresses();
            while (inetAddress.hasMoreElements()) {
                InetAddress currentAddress = inetAddress.nextElement();
                if (currentAddress instanceof Inet4Address
                    || currentAddress instanceof Inet6Address
                    && !currentAddress.isLoopbackAddress()) {
                    System.out.println(currentAddress);
                }
            }
        }
    }

}
