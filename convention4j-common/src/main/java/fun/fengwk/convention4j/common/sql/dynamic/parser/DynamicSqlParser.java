package fun.fengwk.convention4j.common.sql.dynamic.parser;

import fun.fengwk.convention4j.common.sql.dynamic.node.AbstractContainerNode;
import fun.fengwk.convention4j.common.sql.dynamic.node.DynamicSqlNode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

/**
 * @author fengwk
 */
public class DynamicSqlParser {

    public DynamicSqlNode parse(String dynamicSql) throws SAXException {
        if (dynamicSql == null) {
            return null;
        }

        SAXParser parser = buildSaxParser();
        DynamicSqlHandler handler = new DynamicSqlHandler();
        try {
            parser.parse(buildDynamicSqlInputSource(dynamicSql), handler);
        } catch (IOException ex) {
            // 这里直接构建字符串，因此不会涉及IO
            throw new IllegalStateException(ex);
        }

        LinkedList<AbstractContainerNode> nodeStack = handler.getNodeStack();
        return nodeStack.isEmpty() ? null : (DynamicSqlNode) nodeStack.pop();
    }

    private SAXParser buildSaxParser() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            // 拒绝外部的DTD和SCHEMA访问
            // http://www.mainboot.com/manual/java/jdk1.8/javax/xml/XMLConstants.html#ACCESS_EXTERNAL_SCHEMA
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return parser;
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private InputSource buildDynamicSqlInputSource(String dynamicSql) {
        return new InputSource(new StringReader("<dynamicSql>" + dynamicSql + "</dynamicSql>"));
    }

}
