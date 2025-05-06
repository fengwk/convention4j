package fun.fengwk.convention4j.common.http;

import fun.fengwk.convention4j.common.io.IoUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fengwk
 */
public class FormDataBuilderTest {

    @Test
    public void test() throws IOException {
        FormDataBuilder fdb = new FormDataBuilder();
        fdb.addField("name", "fengwk");
        fdb.addField("vvv和", "哈哈");
//        fdb.addFile("pypy", new File("/home/fengwk/tmp/a.py"));
        try (InputStream input = fdb.build()) {
            System.out.println(IoUtils.readString(input));
        }
    }

}
