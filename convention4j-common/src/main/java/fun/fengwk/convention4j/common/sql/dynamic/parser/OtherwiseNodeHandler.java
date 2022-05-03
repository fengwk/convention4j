package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.ChooseNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.OtherwiseNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class OtherwiseNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(qName, attributes)));
        }

        AbstractContainerNode parent = nodeStack.peek();
        if (!(parent instanceof ChooseNode)) {
            throw new SAXException(String.format("parent of %s should be <choose>", buildTag(qName, attributes)));
        }

        OtherwiseNode otherwiseNode = new OtherwiseNode();
        ((ChooseNode) parent).setOtherwise(otherwiseNode);
        return otherwiseNode;
    }

}
