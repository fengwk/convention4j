package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author fengwk
 */
public class DynamicSqlHandler extends DefaultHandler {

    private static final Map<String, AbstractNodeHandler> HANDLER_MAP;

    static {
        Map<String, AbstractNodeHandler> handlerMap = new HashMap<>();
        handlerMap.put("choose", new ChooseNodeHandler());
        handlerMap.put("dynamicSql", new DynamicSqlNodeHandler());
        handlerMap.put("foreach", new ForeachNodeHandler());
        handlerMap.put("if", new IfNodeHandler());
        handlerMap.put("otherwise", new OtherwiseNodeHandler());
        handlerMap.put("set", new SetNodeHandler());
        handlerMap.put("trim", new TrimNodeHandler());
        handlerMap.put("when", new WhenNodeHandler());
        handlerMap.put("where", new WhereNodeHandler());
        HANDLER_MAP = handlerMap;
    }

    private final LinkedList<AbstractContainerNode> nodeStack = new LinkedList<>();
    private final LinkedList<AbstractNodeHandler> handlerStack = new LinkedList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        AbstractNodeHandler nodeHandler = HANDLER_MAP.get(qName);
        if (nodeHandler == null) {
            throw new SAXException(String.format("the NodeHandler for '%s' cannot be found", qName));
        }

        nodeHandler.startElement(nodeStack, uri, localName, qName, attributes);
        handlerStack.push(nodeHandler);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        AbstractNodeHandler nodeHandler = handlerStack.pop();
        nodeHandler.endElement(nodeStack, uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        AbstractNodeHandler nodeHandler = handlerStack.peek();
        assert nodeHandler != null;
        nodeHandler.characters(nodeStack, ch, start, length);
    }

    public LinkedList<AbstractContainerNode> getNodeStack() {
        return nodeStack;
    }

}
