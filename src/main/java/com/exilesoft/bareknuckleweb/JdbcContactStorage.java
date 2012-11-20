package com.exilesoft.bareknuckleweb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcContactStorage implements ContactStorage {

    @Override
    public void createContact(String fullName, String phoneNumber) throws SQLException {
        try (PreparedStatement statement = DataSources.createStatement("insert into contacts (fullName, phoneNumber) values (?, ?)")) {
            statement.setString(1, fullName);
            statement.setString(2, phoneNumber);
            statement.execute();
        }
    }

    @Override
    public List<Contact> find(String nameQuery) throws SQLException {
        return nameQuery != null ? findByNameLike(nameQuery) : findAll();
    }

    private List<Contact> findByNameLike(String nameQuery) throws SQLException {
        try (PreparedStatement statement = DataSources.createStatement("select * from contacts where upper(fullName) like ?")) {
            statement.setString(1, "%" + nameQuery.toUpperCase() + "%");
            try (ResultSet rs = statement.executeQuery()) {
                return mapResultToContacts(rs);
            }
        }
    }

    private List<Contact> findAll() throws SQLException {
        try (ResultSet rs = DataSources.executeQuery("select * from contacts")) {
            return mapResultToContacts(rs);
        }
    }

    private List<Contact> mapResultToContacts(ResultSet rs) throws SQLException {
        List<Contact> result = new ArrayList<>();
        while (rs.next()) {
            result.add(new Contact(rs.getString("fullName"), rs.getString("phoneNumber")));
        }
        return result;
    }

    public void createTable() throws SQLException {
        DataSources.executeUpdate("create table contacts ( fullName varchar(200), phoneNumber varchar(200))");
    }

}
