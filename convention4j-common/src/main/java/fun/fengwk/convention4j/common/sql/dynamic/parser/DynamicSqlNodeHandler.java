package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.DynamicSqlNode;
import org.xml.sax.Attributes;

import java.util.LinkedList;

/**
 * @author fengwk
 */
public class DynamicSqlNodeHandler extends AbstractNodeHandler {

    @Override
    protected AbstractContainerNode newContainerNode(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName, Attributes attributes) {
        return new DynamicSqlNode();
    }

    @Override
    public void endElement(LinkedList<AbstractContainerNode> nodeStack, String uri, String localName, String qName) {
        // nothing to do
    }

}
