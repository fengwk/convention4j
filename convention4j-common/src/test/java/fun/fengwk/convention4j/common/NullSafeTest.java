package fun.fengwk.convention4j.common;

import lombok.Data;
import org.junit.Test;

/**
 * @author fengwk
 */
public class NullSafeTest {

    @Test
    public void test() {
        A a = new A();
        assert NullSafe.map5(a, A::getB, B::getC, C::getD, D::getE, E::getVal) == 123;
        assert NullSafe.map5(a, A::getB, B::getC, C::getD, D::getE, E::getVal2, 0) == 0;
    }

    @Data
    static class A {
        B b = new B();
    }

    @Data
    static class B {
        C c = new C();
    }

    @Data
    static class C {
        D d = new D();
    }

    @Data
    static class D {
        E e = new E();
    }

    @Data
    static class E {
        int val = 123;
        Integer val2;
    }

}
