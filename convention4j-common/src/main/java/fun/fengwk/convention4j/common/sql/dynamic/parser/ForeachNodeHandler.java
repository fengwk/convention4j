package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.ForeachNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;

/**
 *
 * @author fengwk
 */
public class ForeachNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (nodeStack.isEmpty()) {
            throw new SAXException(String.format("%s should have a parent tag", buildTag(qName, attributes)));
        }

        String item = attributes.getValue("item");
        String index = attributes.getValue("index");
        String collection = attributes.getValue("collection");
        String open = attributes.getValue("open");
        String separator = attributes.getValue("separator");
        String close = attributes.getValue("close");

        if (item == null || collection == null) {
            throw new SAXException(String.format("%s should have two attributes 'item' and 'collection'",
                    buildTag(qName, attributes)));
        }

        ForeachNode foreachNode = new ForeachNode(item, index, collection, open, separator, close);
        AbstractContainerNode containerNode = nodeStack.peek();
        assert containerNode != null;
        containerNode.getChildren().add(foreachNode);
        return foreachNode;
    }

}
