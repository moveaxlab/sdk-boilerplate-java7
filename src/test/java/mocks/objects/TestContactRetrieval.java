package mocks.objects;

import it.sdkboilerplate.objects.SdkObject;
import it.sdkboilerplate.validation.Schema;

public class TestContactRetrieval extends SdkObject {
    public String type;
    public String value;

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Schema getSchema() {
        return new Schema();
    }

    public TestContactRetrieval(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public TestContactRetrieval() {
    }


}
