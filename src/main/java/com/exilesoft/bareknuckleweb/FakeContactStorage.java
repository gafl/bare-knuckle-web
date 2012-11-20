package com.exilesoft.bareknuckleweb;

import java.util.ArrayList;
import java.util.List;

public class FakeContactStorage implements ContactStorage {

    private final ArrayList<Contact> contacts = new ArrayList<>();

    @Override
    public void createContact(String fullName, String phoneNumber) {
        contacts.add(new Contact(fullName, phoneNumber));
    }

    @Override
    public List<Contact> find(String nameQuery) {
        return contacts;
    }

}
