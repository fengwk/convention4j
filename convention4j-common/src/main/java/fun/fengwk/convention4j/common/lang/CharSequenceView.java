package fun.fengwk.convention4j.common.lang;

import java.util.Objects;

/**
 * {@link CharSequence}视图。
 *
 * @author fengwk
 */
public class CharSequenceView implements CharSequence {

    private final CharSequence cs;
    private final int lo;
    private final int hi;

    /**
     *
     * @param cs
     * @param lo 开始位置，包含。
     * @param hi 结束位置，排除。
     */
    public CharSequenceView(CharSequence cs, int lo, int hi) {
        if (lo < 0 || lo > Math.min(cs.length(), hi)) {
            throw new IndexOutOfBoundsException(String.format("lo '%d' out of bound [0..%d]", lo, Math.min(cs.length(), hi)));
        }
        if (hi < lo || hi > cs.length()) {
            throw new IndexOutOfBoundsException(String.format("hi '%d' out of bound [%d..%d]", hi, lo, cs.length()));
        }

        this.cs = Objects.requireNonNull(cs);
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public int length() {
        return hi - lo;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || lo + index >= hi) {
            throw new IndexOutOfBoundsException(String.format("index '%d' out of bound [0..%d)", index, length()));
        }

        return cs.charAt(lo + index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if (start < 0 || lo + start > Math.min(hi, lo + end)) {
            throw new IndexOutOfBoundsException(String.format("start '%d' out of bound [0..%d]", start, Math.min(length(), end)));
        }
        if (end < start || lo + end > hi) {
            throw new IndexOutOfBoundsException(String.format("end '%d' out of bound [%d..%d]", end, start, length()));
        }

        return new CharSequenceView(cs, lo + start, lo + end);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = lo; i < hi; i++) {
            sb.append(cs.charAt(i));
        }
        return sb.toString();
    }

}
