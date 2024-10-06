package fun.fengwk.convention4j.common.json;

import com.google.common.collect.*;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.CursorPageQuery;
import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.page.Pages;
import fun.fengwk.convention4j.common.reflect.TypeToken;
import fun.fengwk.convention4j.common.result.Results;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fengwk
 */
public class JsonTest {

    @Test
    public void test1() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = JsonUtils.toJson(list);
        System.out.println(json);
        Object res = JsonUtils.fromJson(json, new TypeToken<ImmutableList<String>>() {});
        assertEquals(list, res);
    }

    @Test
    public void test2() {
        ImmutableSet<String> set = ImmutableSet.of("v1", "v2", "v3");
        String json = JsonUtils.toJson(set);
        assertEquals(JsonUtils.fromJson(json, new TypeToken<ImmutableSet<String>>() {}), set);
        assertEquals(JsonUtils.fromJson(json, new TypeToken<ImmutableSortedSet<String>>() {}), set);
    }

    @Test
    public void test3() {
        ImmutableMap<String, ?> map = ImmutableMap.of("k1", "v1", "k2",
                ImmutableMap.of("k2.k1", "k2.v1", "k2.k2", "k2.v2"));
        String json = JsonUtils.toJson(map);
        assertEquals(JsonUtils.fromJson(json, new TypeToken<ImmutableMap<String, ?>>() {}), map);
        assertEquals(JsonUtils.fromJson(json, new TypeToken<ImmutableSortedMap<String, ?>>() {}), map);
    }

    @Test
    public void test4() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = JsonUtils.toJson(list);
        Object res = JsonUtils.fromJson(json, new TypeToken<ImmutableCollection<String>>() {});
        assertEquals(res, list);
    }

    @Test
    public void test5() {
        String json = "{\"age\":12}";
        Map<String, ?> map = JsonUtils.fromJson(json, new TypeToken<>() {});
        System.out.println(map);
    }

    @Test
    public void test6() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        String json = JsonUtils.toJson(cursorPageQuery);
        CursorPageQuery<Long> fromJsonCursorPageQuery = JsonUtils.fromJson(json, new TypeToken<CursorPageQuery<Long>>() {});
        assertEquals(fromJsonCursorPageQuery, cursorPageQuery);
    }

    @Test
    public void test7() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        List<String> results = Arrays.asList("123", "456");
        CursorPage<String, Long> cursorPage = Pages.cursorPage(cursorPageQuery, results, 3L, true);
        String json = JsonUtils.toJson(cursorPage);
        System.out.println(json);
        CursorPage<String, Long> cursorPage2 = JsonUtils.fromJson(json, new TypeToken<>() {});
        assertEquals(cursorPage2, cursorPage);
    }

    @Test
    public void test8() {
        PageQuery pageQuery = new PageQuery(1, 10);
        String json = JsonUtils.toJson(pageQuery);

        PageQuery pageQuery2 = JsonUtils.fromJson(json, PageQuery.class);
        assertEquals(pageQuery2, pageQuery);
    }

    @Test
    public void test9() {
        PageQuery pageQuery = new PageQuery(1, 10);
        List<String> results = Arrays.asList("123", "456", "789");
        Page<String> page = Pages.page(pageQuery, results, 3);
        String json = JsonUtils.toJson(page);
        Page<String> page2 = JsonUtils.fromJson(json, new TypeToken<>() {});
        assertEquals(page2, page);
    }

    @Test
    public void test10() {
        Result<String> res = Results.ok("ok");
        String resJson = JsonUtils.toJson(res);
        Object res2 = JsonUtils.fromJson(resJson, new TypeToken<Result<String>>() {});
        assertEquals(res, res2);
    }

    @Test
    public void test11() {
        Result<Void> res = Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        String resJson = JsonUtils.toJson(res);
        Object res2 = JsonUtils.fromJson(resJson, new TypeToken<Result<Void>>() {});
        assertEquals(res, res2);
    }

    @Test
    public void test12() {
        Map<String, Object> kvs = new HashMap<>();
        kvs.put("l", Long.MAX_VALUE);
        String s = JsonUtils.toJson(kvs);
        System.out.println(s);
        L lObj = JsonUtils.fromJson(s, L.class);
        assertEquals(kvs.get("l"), lObj.getL());
    }

    @Data
    static class L {
        long l;
    }

}
