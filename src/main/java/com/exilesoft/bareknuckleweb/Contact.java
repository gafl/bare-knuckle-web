package com.exilesoft.bareknuckleweb;

public class Contact {

    private final String fullName;
    private final String phoneNumber;

    public Contact(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String print() {
        return fullName + " (" + phoneNumber + ")";
    }

    @Override
    public String toString() {
        return "Contact<" + print() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact)) return false;
        return ((Contact)obj).print().equals(print());
    }

    @Override
    public int hashCode() {
        return print().hashCode();
    }

}
