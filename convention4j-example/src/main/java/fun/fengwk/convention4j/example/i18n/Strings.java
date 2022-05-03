package fun.fengwk.convention4j.example.i18n;

import fun.fengwk.convention4j.common.i18n.Name;

/**
 * @author fengwk
 */
public interface Strings {

    String greeting(@Name("name") String name);

}
