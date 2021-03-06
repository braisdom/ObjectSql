package com.github.braisdom.objsql.benchmark;

import com.github.braisdom.objsql.ConnectionFactory;
import com.github.braisdom.objsql.Databases;
import com.github.braisdom.objsql.Logger;
import com.github.braisdom.objsql.LoggerFactory;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ObjectiveSQL implements ORMFramework, ConnectionFactory, Logger, LoggerFactory {

    public static final String FRAMEWORK_NAME = "objsql";

    private final HikariDataSource dataSource;

    public ObjectiveSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;

        Databases.installConnectionFactory(this);
        Databases.installLoggerFactory(this);
    }

    @Override
    public void initialize() throws Exception {
        Databases.execute("insert into user(id, name, age) values(1, 'ash', 25)");
    }

    @Override
    public void update() throws Exception {
        User.update("age = 12", "id = ?", 1);
    }

    @Override
    public User query() throws Exception {
        return User.queryByPrimaryKey(1);
    }

    @Override
    public void teardown() {
        dataSource.close();
    }

    @Override
    public Connection getConnection(String dataSourceName) throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void debug(long elapsedTime, String sql, Object[] params) {
        // Do nothing
    }

    @Override
    public void info(long elapsedTime, String sql, Object[] params) {
        // Do nothing
    }

    @Override
    public void error(String message, Throwable throwable) {
        // Do nothing
    }

    @Override
    public Logger create(Class<?> clazz) {
        return new ObjectiveSQL(dataSource);
    }
}
