package fun.fengwk.convention4j.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * ANT模式，具备ANT模式匹配功能。
 *
 * <p>
 * 在ANT模式中可以使用以下三种特殊的模式匹配符号：
 * <ul>
 *     <li>?代表一段路径中的任意一个字符</li>
 *     <li>*代表一段路径中的任意数量字符</li>
 *     <li>**代表任意数量段路径</li>
 * </ul>
 * </p>
 *
 * @author fengwk
 */
public class AntPattern {

    public static final char SEPARATOR = '/';
    public static final char ANY_SINGLE_CHARACTER = '?';
    public static final char ANY_CHARACTER = '*';
    public static final String ANY_SEGMENTS = "**";

    private final String pattern;
    private final String[] patternSegments;

    /**
     * 构建一个ANT模式。
     *
     * @param pattern not null
     */
    public AntPattern(String pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern cannot be null");
        }

        this.pattern = pattern;
        this.patternSegments = split(pattern);
    }

    /**
     * <p>
     * 检查当前ANT模式是否能匹配指定path。
     * </p>
     *
     * <ul>
     * <li>/a/* can match /a/b</li>
     * <li>/a/?? can match /a/ab</li>
     * <li>/a/** can match /a/a/c</li>
     * </ul>
     *
     * <p>另外还需要注意，绝对路径模式之间，或者相对路径模式之间，才能进行比较，否则总是返回false。</p>
     *
     * @param path
     * @return
     */
    public boolean match(String path) {
        if (path == null) {
            return false;
        }

        boolean patternAbs = isAbs(pattern);
        boolean pathAbs =  isAbs(path);
        if (patternAbs != pathAbs) {
            return false;
        }

        String[] pathSegments = split(path);
        return match(pathSegments);
    }

    /**
     * 判断path是否为绝对路径。
     *
     * @param path
     * @return
     */
    private boolean isAbs(String path) {
        return path != null && path.length() > 0 && path.charAt(0) == SEPARATOR;
    }

    /**
     * 从位置lo开始对path按照'/'进行分割。
     *
     * <p>
     * 分隔示例：
     * <ul>
     *     <li>"a/b/c"将被分隔为["a", "b", "c"]。</li>
     *     <li>边界情况1，"/a/b/c"也会被分隔为["a", "b", "c"]。</li>
     *     <li>边界情况2，"/a/b/c//"也会被分隔为["a", "b", "c"]。</li>
     * </ul>
     * </p>
     *
     * @param path
     * @return
     */
    private String[] split(String path) {
        ArrayList<String> segments = new ArrayList<>();
        // [i,j)是发现的segment区间
        // 一个segment从上一个区间结束开始，直到下一个区间开始位置'/'或者path结尾结束
        int i = 0, j = 0;
        for (; j < path.length(); j++) {
            // 如果发现下一个区间的开始符号则尝试检查区间是否有内容，如果有内容则加入segments中
            if (path.charAt(j) == SEPARATOR) {
                if (i == j) {
                    // 区间没有内容，那么仅仅改变区间发现指针的开始位置
                    i = j+1;
                } else {
                    // 区间里有内容，那么将该区间内容加入segments中
                    segments.add(path.substring(i, j));
                    i = j+1;
                }
            }
        }

        // 最后要考虑path结束位置的区间
        if (i < j) {
            segments.add(path.substring(i, j));
        }

        return segments.toArray(new String[0]);
    }

    /**
     * 判断pathSegments是否符合patternSegments指定的模式。
     *
     * <p>
     * 如下是一个模式匹配的例子，通过不断读入path改变state集合，如果读取到path最后的字符，state集合中存在状态5，也就是terminal，那么说明模式匹配成功。
     * 特别的是**可以保持在当前状态，也可以转移到下一个状态，其它的状态如果失配就会丢失，terminal状态在下一次匹配就会丢失。
     * </p>
     * <p>pattern   **  a   **  b   *   {terminal}</p>
     * <p>state     0   1   2   3   4   5</p>
     * <p>path      a   c   b   c   b   c</p>
     *
     * @param pathSegments
     * @return
     */
    private boolean match(String[] pathSegments) {
        // 终止状态
        int terminalState = patternSegments.length;
        // 状态集合，state[i]表示当前集合是否存在i状态
        HashSet<Integer> states = new HashSet<>(pathSegments.length + 1);
        // 初始情况下，处于状态0
        states.add(0);
        // 尝试扩张状态
        epsilon(states, patternSegments, terminalState);

        // 读入pathSegments中的内容
        for (String pathSegment : pathSegments) {
            HashSet<Integer> newStates = new HashSet<>(pathSegments.length + 1);
            for (int state : states) {
                if (state != terminalState) {// 如果terminal状态再读取到输入，那么terminal状态将失效，因此无需处理
                    if (ANY_SEGMENTS.equals(patternSegments[state])) {// **能够与任何pathSegment匹配
                        newStates.add(state);
                        newStates.add(state + 1);
                    } else if (match(patternSegments[state], pathSegment)) {// 检查模式是否匹配
                        newStates.add(state + 1);
                    }
                }
            }
            epsilon(newStates, patternSegments, terminalState);
            states = newStates;
        }

        // 此时已经完成pathSegments读取，检查是否存在终止状态
        return states.contains(terminalState);
    }

    /**
     * 尝试扩张状态，比如当前状态中包含**，那么就能将当前状态扩张到更多状态。
     *
     * @param states
     * @param patternSegments
     * @param terminalState
     */
    private void epsilon(HashSet<Integer> states, String[] patternSegments, int terminalState) {
        int prevSize;
        do {
            prevSize = states.size();
            LinkedList<Integer> buf = null;
            for (int state : states) {
                if (state != terminalState && ANY_SEGMENTS.equals(patternSegments[state])) {
                    buf = tryAddBuf(buf, states, state+1);
                }
            }
            if (buf != null) {
                states.addAll(buf);
            }
        } while (states.size() > prevSize);
    }

    /**
     * 判断pathSegment是否符合patternSegment指定的模式。
     * 注：该方法无需测试，已通过leetcode-44全部用例。
     *
     * @param patternSegment
     * @param pathSegment
     * @return
     */
    private boolean match(String patternSegment, String pathSegment) {
        int terminalState = patternSegment.length();
        HashSet<Integer> states = new HashSet<>(patternSegment.length() + 1);
        states.add(0);
        epsilon(states, patternSegment, terminalState);

        for (int i = 0; i < pathSegment.length(); i++) {
            char c = pathSegment.charAt(i);
            HashSet<Integer> newStates = new HashSet<>(patternSegment.length() + 1);
            for (int state : states) {
                if (state != terminalState) {
                    char pat = patternSegment.charAt(state);
                    if (pat == ANY_CHARACTER) {
                        newStates.add(state);
                        newStates.add(state + 1);
                    } else if (pat == ANY_SINGLE_CHARACTER) {
                        newStates.add(state + 1);
                    } else if (pat == c) {
                        newStates.add(state + 1);
                    }
                }
            }
            epsilon(newStates, patternSegment, terminalState);
            states = newStates;
        }

        return states.contains(terminalState);
    }

    private void epsilon(HashSet<Integer> states, String patternSegment, int terminalState) {
        int prevSize;
        do {
            prevSize = states.size();
            LinkedList<Integer> buf = null;
            for (int state : states) {
                if (state != terminalState && patternSegment.charAt(state) == ANY_CHARACTER) {
                    buf = tryAddBuf(buf, states, state+1);
                }
            }
            if (buf != null) {
                states.addAll(buf);
            }
        } while (states.size() > prevSize);
    }

    /**
     * 如果nextState还没有被状态集states包含，那么会将nextState加入缓冲区buf中，另外如果buf为null则会创建一个全新的缓冲区用于存放nextState。
     *
     * @param buf
     * @param states
     * @param nextState
     * @return
     */
    private LinkedList<Integer> tryAddBuf(LinkedList<Integer> buf, HashSet<Integer> states, int nextState) {
        if (!states.contains(nextState)) {
            if (buf == null) {
                buf = new LinkedList<>();
            }
            buf.add(nextState);
        }
        return buf;
    }

    @Override
    public String toString() {
        return pattern;
    }

}
