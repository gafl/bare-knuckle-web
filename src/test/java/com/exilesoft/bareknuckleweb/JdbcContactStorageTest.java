package com.exilesoft.bareknuckleweb;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdbcContactStorageTest {

    private final ContactStorage storage = new JdbcContactStorage();
    private final TransactionManager transactionManager = new JdbcTransactionManager();

    @BeforeClass
    public static void setupJdbcContactStorage() throws SQLException, IOException {
        JDBCDataSource dataSource = new JDBCDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:contactStorage");
        DataSources.setDataSource(dataSource);

        try (Transaction tx = DataSources.begin()) {
            new JdbcContactStorage().createTable();
        }
    }

    @Test
    public void shouldSaveContact() throws Exception {
        try (Transaction tx = transactionManager.begin()) {
            Contact contact = new Contact("Darth Vader", "6666");
            storeContacts(contact);
            assertThat(storage.find(null)).contains(contact);
        }
    }

    @Test
    public void shouldFindCorrectContacts() throws Exception {
        try (Transaction tx = transactionManager.begin()) {
            Contact matchingContact = new Contact("Darth Vader", "6666");
            Contact nonMatchingContact = new Contact("Anakin Skywalker", "6666");

            storeContacts(matchingContact, nonMatchingContact);
            assertThat(storage.find("darth"))
                .contains(matchingContact)
                .doesNotContain(nonMatchingContact);
        }
    }

    @Test
    public void shouldRollbackUncommittedData() throws Exception {
        Contact uncommittedContact = new Contact("Jar-Jar Binks", "321");
        try (Transaction tx = transactionManager.begin()) {
            storeContacts(uncommittedContact);
        }
        Contact committedContact = new Contact("Luke Skywalker", "123");
        try (Transaction tx = transactionManager.begin()) {
            storeContacts(committedContact);
            tx.setCommit();
        }
        try (Transaction tx = transactionManager.begin()) {
            assertThat(storage.find(null))
                .contains(committedContact).doesNotContain(uncommittedContact);
        }
    }

    private void storeContacts(Contact... contacts) throws Exception {
        for (Contact contact : contacts) {
            storage.createContact(contact.getFullName(), contact.getPhoneNumber());
        }
    }

}
