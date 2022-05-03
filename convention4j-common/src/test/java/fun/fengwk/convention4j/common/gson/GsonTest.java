package fun.fengwk.convention4j.common.gson;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.common.page.CursorPage;
import fun.fengwk.convention4j.common.page.CursorPageQuery;
import fun.fengwk.convention4j.common.page.LitePage;
import fun.fengwk.convention4j.common.page.LitePageQuery;
import fun.fengwk.convention4j.common.page.Page;
import fun.fengwk.convention4j.common.page.PageQuery;
import fun.fengwk.convention4j.common.page.Pages;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public class GsonTest {

    private final Gson gson = GlobalGson.getInstance();

    @Test
    public void test1() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = gson.toJson(list);
        Object res = gson.fromJson(json, new TypeToken<ImmutableList<String>>() {}.getType());
        assert res.equals(list);
    }

    @Test
    public void test2() {
        ImmutableSet<String> set = ImmutableSet.of("v1", "v2", "v3");
        String json = gson.toJson(set);
        assert gson.fromJson(json, new TypeToken<ImmutableSet<String>>() {}.getType()).equals(set);
        assert gson.fromJson(json, new TypeToken<ImmutableSortedSet<String>>() {}.getType()).equals(set);
    }

    @Test
    public void test3() {
        ImmutableMap<String, ?> map = ImmutableMap.of("k1", "v1", "k2",
                ImmutableMap.of("k2.k1", "k2.v1", "k2.k2", "k2.v2"));
        String json = gson.toJson(map);
        assert gson.fromJson(json, new TypeToken<ImmutableMap<String, ?>>() {}.getType()).equals(map);
        assert gson.fromJson(json, new TypeToken<ImmutableSortedMap<String, ?>>() {}.getType()).equals(map);
    }

    @Test
    public void test4() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = gson.toJson(list);
        Object res = gson.fromJson(json, new TypeToken<ImmutableCollection<String>>() {}.getType());
        assert res.equals(list);
    }

    @Test
    public void test5() {
        String json = "{\"age\":12}";
        Map<String, ?> map = gson.fromJson(json, new TypeToken<Map<String, ?>>() {}.getType());
        System.out.println(map);
    }

    @Test
    public void test6() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        String json = gson.toJson(cursorPageQuery);
        CursorPageQuery<Long> fromJsonCursorPageQuery = gson.fromJson(json, new TypeToken<CursorPageQuery<Long>>() {}.getType());
        assert fromJsonCursorPageQuery.equals(cursorPageQuery);
    }

    @Test
    public void test7() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        List<String> results = Arrays.asList("123", "456");
        CursorPage<String, Long> cursorPage = Pages.cursorPage(cursorPageQuery, results, 3L, true);
        String json = gson.toJson(cursorPage);
        CursorPage<String, Long> cursorPage2 = gson.fromJson(json, new TypeToken<CursorPage<String, Long>>() {}.getType());
        assert cursorPage2.equals(cursorPage);
    }

    @Test
    public void test9() {
        LitePageQuery litePageQuery = new LitePageQuery(1, 10);
        String json = gson.toJson(litePageQuery);

        LitePageQuery litePageQuery2 = gson.fromJson(json, LitePageQuery.class);
        assert litePageQuery2.equals(litePageQuery);
    }

    @Test
    public void test10() {
        LitePageQuery litePageQuery = new LitePageQuery(1, 2);
        List<String> results = Arrays.asList("123", "456", "789");
        LitePage<String> litePage = Pages.litePage(litePageQuery, results);
        String json = gson.toJson(litePage);
        LitePage<String> litePage2 = gson.fromJson(json, new TypeToken<LitePage<String>>() {}.getType());
        assert litePage2.equals(litePage);
    }

    @Test
    public void test11() {
        PageQuery pageQuery = new PageQuery(1, 10);
        String json = gson.toJson(pageQuery);

        PageQuery pageQuery2 = gson.fromJson(json, PageQuery.class);
        assert pageQuery2.equals(pageQuery);
    }

    @Test
    public void test12() {
        PageQuery pageQuery = new PageQuery(1, 10);
        List<String> results = Arrays.asList("123", "456", "789");
        Page<String> page = Pages.page(pageQuery, results, 3);
        String json = gson.toJson(page);
        Page<String> page2 = gson.fromJson(json, new TypeToken<Page<String>>() {}.getType());
        assert page2.equals(page);
    }

}
