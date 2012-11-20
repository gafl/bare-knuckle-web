package com.exilesoft.bareknuckleweb;

import java.sql.SQLException;

public interface TransactionManager {

    Transaction begin() throws SQLException;


}
