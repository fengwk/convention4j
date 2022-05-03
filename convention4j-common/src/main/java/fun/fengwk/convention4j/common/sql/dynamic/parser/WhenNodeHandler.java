package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.ChooseNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.WhenNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class WhenNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(localName, attributes)));
        }

        AbstractContainerNode parent = nodeStack.peek();
        if (!(parent instanceof ChooseNode)) {
            throw new SAXException(String.format("parent of %s should be <choose>", buildTag(localName, attributes)));
        }

        String test = attributes.getValue("test");
        if (test == null) {
            throw new SAXException(String.format("%s should have 'test' attribute", buildTag(localName, attributes)));
        }

        WhenNode whenNode = new WhenNode(test);
        parent.getChildren().add(whenNode);
        return whenNode;
    }

}
