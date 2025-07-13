package fun.fengwk.convention4j.springboot.starter.datasource.multi;

/**
 * @author fengwk
 */
public class MultiDataSourceUtils {

    private static final String DATA_SOURCE_NAME_SUFFIX = "DataSource";
    private static final String TRANSACTION_MANAGER_NAME_SUFFIX = "TransactionManager";
    private static final String TRANSACTION_TEMPLATE_NAME_SUFFIX = "TransactionTemplate";
    private static final String SQL_SESSION_FACTORY_NAME_SUFFIX = "SqlSessionFactory";
    private static final String SQL_SESSION_TEMPLATE_NAME_SUFFIX = "SqlSessionTemplate";

    private MultiDataSourceUtils() {}

    public static String buildDataSourceName(String baseName) {
        return baseName + DATA_SOURCE_NAME_SUFFIX;
    }

    public static String buildTransactionManagerName(String baseName) {
        return baseName + TRANSACTION_MANAGER_NAME_SUFFIX;
    }

    public static String buildTransactionTemplateName(String baseName) {
        return baseName + TRANSACTION_TEMPLATE_NAME_SUFFIX;
    }

    public static String buildSqlSessionFactoryName(String baseName) {
        return baseName + SQL_SESSION_FACTORY_NAME_SUFFIX;
    }

    public static String buildSqlSessionTemplateName(String baseName) {
        return baseName + SQL_SESSION_TEMPLATE_NAME_SUFFIX;
    }

}
