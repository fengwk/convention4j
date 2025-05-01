package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.lang.StringUtils;
import fun.fengwk.convention4j.common.tika.ThreadLocalTika;
import org.apache.tika.Tika;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static fun.fengwk.convention4j.common.http.HttpUtils.DEFAULT_CHARSET;

/**
 * form-data请求体构建器（依赖tika-core、spring-core）
 *
 * @see <a href="https://www.cnblogs.com/throwable/p/15740444.html">理解HTTP协议中的multipart/form-data</a>
 * @author fengwk
 */
public class FormDataBuilder {

    private static final String CRLF = "\r\n";

    private final String boundary;
    private final List<InputStream> parts;

    public static String generateBoundary() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public FormDataBuilder() {
        this(generateBoundary());
    }

    public FormDataBuilder(String boundary) {
        this.boundary = boundary;
        this.parts = new ArrayList<>();
    }

    /**
     * 添加一个字段
     *
     * @param name 字段名
     * @param value 字段值
     * @throws IllegalArgumentException name或value为空
     */
    public void addField(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }

        appendBoundaryLine();
        appendContentDisposition(name, null);
        appendContentType("text/plain;charset=" + DEFAULT_CHARSET.name());
        appendCRLF();
        append(value.getBytes(DEFAULT_CHARSET));
        appendCRLF();
    }

    /**
     * 通过文件方式添加
     *
     * @param name 名称
     * @param file 文件
     * @throws IllegalArgumentException name或file为空
     * @throws FileNotFoundException 找不到文件
     * @throws IOException 打开或读取文件错误
     */
    public void addFile(String name, File file) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        Tika tika = ThreadLocalTika.current();
        String mimeType = tika.detect(file);
        addFile(name, file.getName(), new FileInputStream(file), mimeType);
    }

    /**
     * 通过流方式添加文件
     *
     * @param name 名称
     * @param fileName 文件名
     * @param inputStream 文件输入流
     * @param mimeType 文件mime类型
     * @throws IllegalArgumentException name或inputStream为空
     */
    public void addFile(String name, String fileName, InputStream inputStream, String mimeType) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream must not be null");
        }

        if (StringUtils.isBlank(mimeType)) {
            mimeType = "application/octet-stream";
        }

        appendBoundaryLine();
        appendContentDisposition(name, fileName);
        appendContentType(mimeType);
        appendCRLF();
        append(inputStream);
        appendCRLF();
    }

    /**
     * 构建最终的form-data输入流
     *
     * @return form-data输入流
     */
    public InputStream build() {
        List<InputStream> streams = new ArrayList<>(parts);
        String endBoundary = "--" + boundary + "--" + CRLF;
        streams.add(new ByteArrayInputStream(endBoundary.getBytes(DEFAULT_CHARSET)));
        return new SequenceInputStream(Collections.enumeration(streams));
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    private void appendBoundaryLine() {
        appendLine("--" + boundary);
    }

    private void appendBoundaryEndLine() {
        appendLine("--" + boundary + "--");
    }

    private void appendContentDisposition(String name, String filename) {
        StringBuilder lineSb = new StringBuilder("Content-Disposition: form-data");
        String encodedName = HttpUtils.encodeUrlComponent(name, DEFAULT_CHARSET);
        lineSb.append("; name=\"").append(encodedName).append("\"");
        if (StringUtils.isNotEmpty(filename)) {
            String encodedFilename = HttpUtils.encodeUrlComponent(filename, DEFAULT_CHARSET);
            lineSb.append("; filename=\"").append(encodedFilename).append("\"");
        }
        appendLine(lineSb.toString());
    }

    private void appendContentType(String mimeType) {
        appendLine("Content-Type: " + mimeType);
    }

    private void appendLine(String line) {
        append(line.getBytes(DEFAULT_CHARSET));
        appendCRLF();
    }

    private void appendCRLF() {
        append(CRLF.getBytes(DEFAULT_CHARSET));
    }

    private void append(byte[] bytes) {
        parts.add(new ByteArrayInputStream(bytes));
    }

    private void append(InputStream input) {
        parts.add(input);
    }

}
