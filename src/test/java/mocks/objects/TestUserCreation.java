package mocks.objects;

import it.sdkboilerplate.objects.SdkObject;
import it.sdkboilerplate.validation.Schema;

public class TestUserCreation extends SdkObject {
    public String name;
    public String surname;

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public TestUserCreation(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Schema getSchema() {
        return new Schema();
    }


    public TestUserCreation() {
    }
}
