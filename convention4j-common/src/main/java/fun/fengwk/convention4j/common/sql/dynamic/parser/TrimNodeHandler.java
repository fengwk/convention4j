package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.TrimNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class TrimNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(localName, attributes)));
        }

        String prefix = attributes.getValue("prefix");
        String suffix = attributes.getValue("suffix");
        String prefixOverrides = attributes.getValue("prefixOverrides");
        String suffixOverrides = attributes.getValue("suffixOverrides");

        if (prefix == null && suffix == null && prefixOverrides == null && suffixOverrides == null) {
            throw new SAXException(String.format("%s contains at least one of the four attributes " +
                    "prefix, suffix, prefixOverrides and suffixOverrides", buildTag(localName, attributes)));
        }

        TrimNode trimNode = new TrimNode(prefix, suffix, prefixOverrides, suffixOverrides);
        AbstractContainerNode containerNode = nodeStack.peek();
        assert containerNode != null;
        containerNode.getChildren().add(trimNode);
        return trimNode;
    }

}
