package fun.fengwk.convention4j.springboot.test.starter.mockito;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @see <a href="https://juejin.cn/post/6844903812101046286">Java单元测试神器之Mockito</a>
 * @author fengwk
 */
public class MockitoTest {

    @Test
    public void test() {
        List mockList = mock(ArrayList.class);
        assert mockList instanceof ArrayList;

        when(mockList.add("fengwk")).thenReturn(true);
        when(mockList.add("xiaoming")).thenReturn(false);
        assert mockList.add("fengwk");
        assert !mockList.add("xiaoming");
    }

}
