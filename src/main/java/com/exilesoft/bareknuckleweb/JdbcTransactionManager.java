package com.exilesoft.bareknuckleweb;

import java.sql.SQLException;

public class JdbcTransactionManager implements TransactionManager {

    @Override
    public Transaction begin() throws SQLException {
        return DataSources.begin();
    }

}
