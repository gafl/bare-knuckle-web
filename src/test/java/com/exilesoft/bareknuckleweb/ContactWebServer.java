package com.exilesoft.bareknuckleweb;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hsqldb.jdbc.JDBCDataSource;

public class ContactWebServer {

    public static void main(String[] args) throws Exception {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:file:target/test.db");
        DataSources.setDataSource(dataSource);

        Server server = new Server(10080);
        server.setHandler(new WebAppContext("src/main/webapp", "/phonebook"));
        server.start();
    }

}
