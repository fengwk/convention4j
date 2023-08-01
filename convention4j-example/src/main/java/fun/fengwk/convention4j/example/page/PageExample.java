package fun.fengwk.convention4j.example.page;

import fun.fengwk.convention4j.api.page.Page;
import fun.fengwk.convention4j.api.page.PageQuery;
import fun.fengwk.convention4j.common.page.Pages;

import java.util.Collections;

/**
 * @author fengwk
 */
public class PageExample {

    public static void main(String[] args) {
        PageQuery pageQuery = new PageQuery(1, 10);
        Page<Object> page = Pages.page(pageQuery, Collections.emptyList(), 0);
    }

}
