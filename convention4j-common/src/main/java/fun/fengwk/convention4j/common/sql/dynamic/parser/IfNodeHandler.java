package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.IfNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class IfNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(qName, attributes)));
        }

        String test = attributes.getValue("test");
        if (test == null) {
            throw new SAXException(String.format("%s should have 'test' attribute", buildTag(localName, attributes)));
        }

        IfNode ifNode = new IfNode(test);
        AbstractContainerNode containerNode = nodeStack.peek();
        assert containerNode != null;
        containerNode.getChildren().add(ifNode);
        return ifNode;
    }

}
