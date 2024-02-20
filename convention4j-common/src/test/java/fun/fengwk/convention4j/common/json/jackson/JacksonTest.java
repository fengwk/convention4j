package fun.fengwk.convention4j.common.json.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.*;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.CursorPageQuery;
import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.page.Pages;
import fun.fengwk.convention4j.common.result.Results;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author fengwk
 */
public class JacksonTest {

    @Test
    public void test1() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = JacksonUtils.writeValueAsString(list);
        System.out.println(json);
        Object res = JacksonUtils.readValue(json, new TypeReference<ImmutableList<String>>() {});
        assertEquals(list, res);
    }

    @Test
    public void test2() {
        ImmutableSet<String> set = ImmutableSet.of("v1", "v2", "v3");
        String json = JacksonUtils.writeValueAsString(set);
        assertEquals(JacksonUtils.readValue(json, new TypeReference<ImmutableSet<String>>() {}), set);
        assertEquals(JacksonUtils.readValue(json, new TypeReference<ImmutableSortedSet<String>>() {}), set);
    }

    @Test
    public void test3() {
        ImmutableMap<String, ?> map = ImmutableMap.of("k1", "v1", "k2",
                ImmutableMap.of("k2.k1", "k2.v1", "k2.k2", "k2.v2"));
        String json = JacksonUtils.writeValueAsString(map);
        assertEquals(JacksonUtils.readValue(json, new TypeReference<ImmutableMap<String, ?>>() {}), map);
        assertEquals(JacksonUtils.readValue(json, new TypeReference<ImmutableSortedMap<String, ?>>() {}), map);
    }

    @Test
    public void test4() {
        ImmutableList<String> list = ImmutableList.of("v1", "v2", "v3");
        String json = JacksonUtils.writeValueAsString(list);
        Object res = JacksonUtils.readValue(json, new TypeReference<ImmutableCollection<String>>() {});
        assertEquals(res, list);
    }

    @Test
    public void test5() {
        String json = "{\"age\":12}";
        Map<String, ?> map = JacksonUtils.readValue(json, new TypeReference<Map<String, ?>>() {});
        System.out.println(map);
    }

    @Test
    public void test6() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        String json = JacksonUtils.writeValueAsString(cursorPageQuery);
        CursorPageQuery<Long> fromJsonCursorPageQuery = JacksonUtils.readValue(json, new TypeReference<CursorPageQuery<Long>>() {});
        assertEquals(fromJsonCursorPageQuery, cursorPageQuery);
    }

    @Test
    public void test7() {
        CursorPageQuery<Long> cursorPageQuery = new CursorPageQuery<>(1L, 10);
        List<String> results = Arrays.asList("123", "456");
        CursorPage<String, Long> cursorPage = Pages.cursorPage(cursorPageQuery, results, 3L, true);
        String json = JacksonUtils.writeValueAsString(cursorPage);
        System.out.println(json);
        CursorPage<String, Long> cursorPage2 = JacksonUtils.readValue(json, new TypeReference<CursorPage<String, Long>>() {});
        assertEquals(cursorPage2, cursorPage);
    }

    @Test
    public void test8() {
        PageQuery pageQuery = new PageQuery(1, 10);
        String json = JacksonUtils.writeValueAsString(pageQuery);

        PageQuery pageQuery2 = JacksonUtils.readValue(json, PageQuery.class);
        assertEquals(pageQuery2, pageQuery);
    }

    @Test
    public void test9() {
        PageQuery pageQuery = new PageQuery(1, 10);
        List<String> results = Arrays.asList("123", "456", "789");
        Page<String> page = Pages.page(pageQuery, results, 3);
        String json = JacksonUtils.writeValueAsString(page);
        Page<String> page2 = JacksonUtils.readValue(json, new TypeReference<Page<String>>() {});
        assertEquals(page2, page);
    }

    @Test
    public void test10() {
        Result<String> res = Results.ok("ok");
        String resJson = JacksonUtils.writeValueAsString(res);
        Object res2 = JacksonUtils.readValue(resJson, new TypeReference<Result<String>>() {});
        assertEquals(res, res2);
    }

    @Test
    public void test11() {
        Result<Void> res = Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        String resJson = JacksonUtils.writeValueAsString(res);
        Object res2 = JacksonUtils.readValue(resJson, new TypeReference<Result<Void>>() {});
        assertEquals(res, res2);
    }

    @Test
    public void test12() {
        Map<String, Object> kvs = new HashMap<>();
        kvs.put("l", Long.MAX_VALUE);
        String s = JacksonUtils.writeValueAsString(kvs);
        System.out.println(s);
        L lObj = JacksonUtils.readValue(s, L.class);
        assertEquals(kvs.get("l"), lObj.getL());
        System.out.println(JacksonUtils.writeValueAsString(lObj));
    }

    @Data
    static class L {
        long l;
    }



}
