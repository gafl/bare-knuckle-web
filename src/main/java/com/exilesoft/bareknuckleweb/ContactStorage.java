package com.exilesoft.bareknuckleweb;

import java.sql.SQLException;
import java.util.List;

public interface ContactStorage {

    void createContact(String fullName, String phoneNumber) throws Exception;

    List<Contact> find(String nameQuery) throws SQLException;

}
