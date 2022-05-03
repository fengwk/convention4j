package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.SetNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class SetNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(qName, attributes)));
        }

        SetNode setNode = new SetNode();
        AbstractContainerNode containerNode = nodeStack.peek();
        assert containerNode != null;
        containerNode.getChildren().add(setNode);
        return setNode;
    }

}
