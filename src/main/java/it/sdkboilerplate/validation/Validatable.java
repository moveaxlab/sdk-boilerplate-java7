package it.sdkboilerplate.validation;

import it.sdkboilerplate.exceptions.JsonSerializationException;

public interface Validatable {
    Schema getSchema() throws JsonSerializationException;
}
