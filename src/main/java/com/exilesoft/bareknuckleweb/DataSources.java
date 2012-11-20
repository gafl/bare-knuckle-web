package com.exilesoft.bareknuckleweb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class DataSources {

    private static ThreadLocal<Connection> threadConnection = new ThreadLocal<Connection>();
    private static DataSource dataSource;

    public static PreparedStatement createStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    private static Connection getConnection() {
        return threadConnection.get();
    }

    public static Transaction begin() throws SQLException {
        final Connection connection = getDataSource().getConnection();
        threadConnection.set(connection);
        connection.setAutoCommit(false);
        return new Transaction() {
            private boolean doCommit = false;

            @Override
            public void setCommit() {
                doCommit = true;
            }

            @Override
            public void close() throws IOException {
                threadConnection.set(null);
                try {
                    if (doCommit)   connection.commit();
                    else            connection.rollback();
                    connection.close();
                } catch (SQLException e) {
                    throw new IOException(e);
                }
            }
        };
    }

    private static DataSource getDataSource() {
        return dataSource;
    }

    public static void setDataSource(DataSource dataSource) {
        DataSources.dataSource = dataSource;
    }

    public static void executeUpdate(String sql) throws SQLException {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public static ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = getConnection().createStatement();
        return stmt.executeQuery(sql);
    }

}
