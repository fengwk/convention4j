package fun.fengwk.convention4j.springboot.starter.transport;

import fun.fengwk.convention4j.common.web.XForwardedHeader;

/**
 * @author fengwk
 */
public class XForwardedHeaderTransportHeadersModifier implements TransportHeadersModifier {

    @Override
    public void modify(TransportHeaders transportHeaders) {
        for (XForwardedHeader header : XForwardedHeader.values()) {
            transportHeaders.addHeader(header.getName());
        }
    }

}
