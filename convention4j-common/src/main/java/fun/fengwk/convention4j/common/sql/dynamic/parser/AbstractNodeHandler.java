package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.FragmentNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public abstract class AbstractNodeHandler {

    public void startElement(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        nodeStack.push(newContainerNode(nodeStack, uri, localName, qName, attributes));
    }

    public void endElement(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName) {
        nodeStack.pop();
    }

    void characters(LinkedList<AbstractContainerNode> nodeStack, char[] ch, int start, int length) throws SAXException {
        String fragment = new String(ch, start, length);
        if (StringUtils.isNotBlank(fragment)) {
            FragmentNode fragmentNode = new FragmentNode(fragment);
            AbstractContainerNode containerNode = nodeStack.peek();
            assert containerNode != null;
            containerNode.getChildren().add(fragmentNode);
        }
    }

    protected abstract AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException;

    protected String buildTag(String qName, Attributes attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            sb.append(" ").append(attributes.getLocalName(i)).append("=\"").append(attributes.getValue(i)).append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

}
