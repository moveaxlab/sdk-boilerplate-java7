package mocks.objects;

import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.objects.SdkObject;
import it.sdkboilerplate.validation.Schema;

import java.util.HashMap;


public class TestUserRetrieval extends SdkObject {
    private String name;
    private String surname;

    private TestContactRetrieval contact;
    private TestAccountsCollection accounts;

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public void setContact(TestContactRetrieval contact) {
        this.contact = contact;
    }

    public void setAccounts(TestAccountsCollection accounts) {
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public TestContactRetrieval getContact() {
        return contact;
    }

    public TestAccountsCollection getAccounts() {
        return accounts;
    }

    public Schema getSchema() {
        return new Schema();
    }


    public static HashMap<String, Class<? extends SdkBodyType>> getSubObjects() {
        HashMap<String, Class<? extends SdkBodyType>> subObjects = new HashMap();
        subObjects.put("contact", TestContactRetrieval.class);
        subObjects.put("accounts", TestAccountsCollection.class);
        return subObjects;
    }

    public TestUserRetrieval(String name, String surname, TestContactRetrieval contact, TestAccountsCollection accounts) {
        this.name = name;
        this.surname = surname;
        this.contact = contact;
        this.accounts = accounts;
    }

    public TestUserRetrieval() {
    }
}
