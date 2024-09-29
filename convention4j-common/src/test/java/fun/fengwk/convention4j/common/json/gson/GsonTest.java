package fun.fengwk.convention4j.common.json.gson;

import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fun.fengwk.convention4j.api.code.CommonErrorCodes;
import fun.fengwk.convention4j.api.page.CursorPage;
import fun.fengwk.convention4j.api.page.CursorPageQuery;
import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.api.result.Result;
import fun.fengwk.convention4j.common.page.Pages;
import fun.fengwk.convention4j.common.result.Results;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author fengwk
 */
public class GsonTest {

    private final Gson gson = GsonHolder.getInstance();

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
    public void test8() {
        PageQuery pageQuery = new PageQuery(1, 10);
        String json = gson.toJson(pageQuery);

        PageQuery pageQuery2 = gson.fromJson(json, PageQuery.class);
        assert pageQuery2.equals(pageQuery);
    }

    @Test
    public void test9() {
        PageQuery pageQuery = new PageQuery(1, 10);
        List<String> results = Arrays.asList("123", "456", "789");
        Page<String> page = Pages.page(pageQuery, results, 3);
        String json = gson.toJson(page);
        Page<String> page2 = gson.fromJson(json, new TypeToken<Page<String>>() {}.getType());
        assert page2.equals(page);
    }

    @Test
    public void test10() {
        Result<String> res = Results.ok("ok");
        String resJson = gson.toJson(res);
        Object res2 = gson.fromJson(resJson, new TypeToken<Result<String>>() {}.getType());
        assert res.equals(res2);
    }

    @Test
    public void test11() {
        Result<Void> res = Results.error(CommonErrorCodes.INTERNAL_SERVER_ERROR);
        String resJson = gson.toJson(res);
        Object res2 = gson.fromJson(resJson, new TypeToken<Result<Void>>() {}.getType());
        assert res.equals(res2);
    }

    @Test
    public void test12() {
        Result<String> res = Results.ok("ok");
        StringWriter w = new StringWriter();
        gson.toJson(res, Result.class, w);
        System.out.println(w.toString());
        assert "{\"status\":200,\"code\":\"OK\",\"message\":\"OK\",\"data\":\"ok\"}".equals(w.toString());
    }

    @Test
    public void test13() {
        StringWriter w = new StringWriter();
        Page<Void> page = Pages.emptyPage(new PageQuery(1, 10));
        gson.toJson(page, Page.class, w);
        assert "{\"pageNumber\":1,\"pageSize\":10,\"results\":[],\"totalCount\":\"0\"}".equals(w.toString());
    }

    @Test
    public void test14() {
        StringWriter w = new StringWriter();
        CursorPage<Void, String> cursorPage = Pages.emptyCursorPage(new CursorPageQuery<>(null, 10));
        gson.toJson(cursorPage, CursorPage.class, w);
        assert "{\"limit\":10,\"results\":[],\"more\":false}".equals(w.toString());
    }

}
