package fun.fengwk.convention4j.springboot.starter.datasource.multi;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * @author fengwk
 */
@Slf4j
public class HotReplaceDataSource implements DataSource, ApplicationListener<HotReplaceDataSourceEvent>, AutoCloseable {

    private final String name;
    private final Function<DataSourceConfig, HikariDataSource> dataSourceFactory;
    private volatile HikariDataSource delegate;
    private boolean closed;

    public HotReplaceDataSource(String name,
                                Function<DataSourceConfig, HikariDataSource> dataSourceFactory,
                                DataSourceConfig source) {
        Assert.notNull(name, "name must not be null");
        Assert.notNull(dataSourceFactory, "dataSourceFactory must not be null");
        Assert.notNull(source, "source must not be null");
        this.name = name;
        this.dataSourceFactory = dataSourceFactory;
        this.delegate = dataSourceFactory.apply(source);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return delegate.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegate.getParentLogger();
    }

    @Override
    public ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return delegate.createShardingKeyBuilder();
    }

    @Override
    public ConnectionBuilder createConnectionBuilder() throws SQLException {
        return delegate.createConnectionBuilder();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }

    @Override
    public synchronized void onApplicationEvent(HotReplaceDataSourceEvent event) {
        if (Objects.equals(name, event.getName()) && !closed) {
            HikariDataSource oldDataSource = this.delegate;
            HikariDataSource newDataSource = dataSourceFactory.apply(event.getDataSourceConfig());
            this.delegate = newDataSource;
            log.info("dataSource hot replaced, name: {}, newDataSource: {}, oldDataSource: {}",
                name, newDataSource, oldDataSource);
            oldDataSource.close();
        }
    }

    @Override
    public synchronized void close() {
        if (!closed) {
            delegate.close();
            this.closed = true;
        }
    }

}
