package fun.fengwk.convention4j.example.page;

import fun.fengwk.convention4j.common.page.LitePage;
import fun.fengwk.convention4j.common.page.LitePageQuery;
import fun.fengwk.convention4j.common.page.Pages;

import java.util.Collections;

/**
 * @author fengwk
 */
public class LitePageExample {

    public static void main(String[] args) {
        LitePageQuery litePageQuery = new LitePageQuery(1, 10);
        LitePage<Object> litePage = Pages.litePage(litePageQuery, Collections.emptyList());
    }

}
