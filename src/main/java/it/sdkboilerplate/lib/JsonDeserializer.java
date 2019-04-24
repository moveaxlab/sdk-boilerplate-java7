package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.DeserializationException;
import it.sdkboilerplate.exceptions.JsonSerializationException;
import it.sdkboilerplate.exceptions.MalformedSdkObjectException;
import it.sdkboilerplate.exceptions.UnknownBodyTypeException;
import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.objects.SdkCollection;
import it.sdkboilerplate.objects.SdkObject;
import it.sdkboilerplate.utils.Json;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JsonDeserializer implements Deserializer {
    /**
     * Deserialize a jsonString into an SdkBodyType subclass instance
     *
     * @param jsonString  String in json format
     * @param objectClass Class of the deserialization instance
     * @return SdkBodyType subclass instance
     * @throws UnknownBodyTypeException If the given class is undefined
     */
    @Override
    public SdkBodyType deserialize(String jsonString, Class<? extends SdkBodyType> objectClass) throws UnknownBodyTypeException, DeserializationException {
        if (objectClass.getSuperclass() == SdkObject.class) {
            return this.deserializeSdkObject(jsonString, (Class<? extends SdkObject>) objectClass);
        } else if (objectClass.getSuperclass() == SdkCollection.class) {
            return this.deserializeSdkCollection(jsonString, (Class<? extends SdkCollection>) objectClass);
        } else {
            throw new UnknownBodyTypeException();
        }

    }


    /**
     * Deserialize a json string into an SdkObject instance
     */
    private SdkObject deserializeSdkObject(String jsonString, Class<? extends SdkObject> objectClass)
            throws DeserializationException {

        HashMap<String, Class<? extends SdkBodyType>> subObjects = this.getSubObjects(objectClass);
        HashMap<String, Object> jsonObject = (HashMap<String, Object>) this.loadJson(jsonString, HashMap.class);
        if (jsonObject == null) return null;
        SdkObject instance = this.getInstance(objectClass);
        for (Map.Entry jsonObjectAttribute : jsonObject.entrySet()) {
            String attributeName = WordUtils.capitalizeFully(jsonObjectAttribute.getKey().toString(), new char[]{'_'}).replaceAll("_", "");
            Class<? extends SdkBodyType> subObjectClass = subObjects.get(jsonObjectAttribute.getKey().toString());
            if (subObjectClass != null) {
                String jsonField = this.dumpJson(jsonObjectAttribute.getValue());
                Object attributeValue = this.deserializeSubObject(jsonField, subObjectClass);
                this.setAttribute(instance, attributeName, subObjectClass, attributeValue);
            } else {
                Object attributeValue = jsonObjectAttribute.getValue();
                if (attributeValue != null) {
                    Class attributeClass = attributeValue.getClass();
                    this.setAttribute(instance, attributeName, attributeClass, jsonObjectAttribute.getValue());
                }
            }

        }
        return instance;

    }

    /**
     * Helper method to deserialize a subObject json serialized based on its class and set it as the de-serialized parent
     * object attribute
     */
    private Object deserializeSubObject(String jsonAttribute, Class<? extends SdkBodyType> subObjectClass)
            throws DeserializationException {
        Object value;
        if (subObjectClass.getSuperclass() == SdkObject.class) {
            value = this.deserializeSdkObject(jsonAttribute, (Class<? extends SdkObject>) subObjectClass);
        } else if (subObjectClass.getSuperclass() == SdkCollection.class) {
            value = this.deserializeSdkCollection(jsonAttribute, (Class<? extends SdkCollection>) subObjectClass);
        } else {
            throw new DeserializationException("Unknown object subclass " + subObjectClass);
        }
        return value;

    }

    private SdkObject getInstance(Class<? extends SdkObject> objectClass) {
        try {
            Constructor<? extends SdkObject> objectConstructor = objectClass.getConstructor();
            // Creates a new Instance
            return objectClass.cast(objectConstructor.newInstance());
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InstantiationException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }
    }

    /**
     * Sets an attribute to an SdkObject instance by reflection
     */
    private void setAttribute(SdkObject instance, String attributeName, Class<?> attributeType, Object attributeValue) {
        try {
            Method setter = instance.getClass().getMethod("set" + attributeName, attributeType);
            setter.invoke(instance, attributeValue);
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }
    }

    /**
     * Helper method to retrieve subObjects by reflection on an SdkObject subclass
     */
    private HashMap<String, Class<? extends SdkBodyType>> getSubObjects(Class<? extends SdkObject> objectClass) {
        try {
            return (HashMap<String, Class<? extends SdkBodyType>>) objectClass.getMethod("getSubObjects").invoke(objectClass);
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }
    }

    private Object loadJson(String jsonString, Class<?> targetClass) throws DeserializationException {
        try {
            return Json.load(jsonString, targetClass);
        } catch (JsonSerializationException e) {
            throw new DeserializationException(e.getMessage());
        }
    }

    private String dumpJson(Object object) throws DeserializationException {
        try {
            return Json.dump(object);
        } catch (JsonSerializationException e) {
            throw new DeserializationException(e.getMessage());
        }
    }

    /**
     * Deserialize a json string into an SdkCollection instance
     */
    private SdkCollection deserializeSdkCollection(String jsonString, Class<? extends SdkCollection> collectionClass)
            throws DeserializationException {
        ArrayList<Object> jsonArray = (ArrayList<Object>) this.loadJson(jsonString, ArrayList.class);
        ArrayList<Object> collectionArguments = new ArrayList();
        Class elementsClass = this.getElementsClass(collectionClass);
        for (Object element : jsonArray) {
            if (elementsClass.getSuperclass() == SdkCollection.class) {
                // If the elements are of SdkCollection Type, de-serializes every element as an SdkCollection
                String tmpField = this.dumpJson(new ArrayList().add(element));
                collectionArguments.add(this.deserializeSdkCollection(tmpField, (Class<? extends SdkCollection>) elementsClass));
            } else if (elementsClass.getSuperclass() == SdkObject.class) {
                String tmpField = this.dumpJson(element);
                collectionArguments.add(this.deserializeSdkObject(tmpField, (Class<? extends SdkObject>) elementsClass));
            } else {
                collectionArguments.add(element);
            }
        }
        try {
            Constructor<? extends SdkCollection> constructor = collectionClass.getConstructor(ArrayList.class);
            return constructor.newInstance(collectionArguments);
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InstantiationException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }

    }

    private Class getElementsClass(Class<?> collectionClass) {
        try {
            Method getElementsClass = collectionClass.getMethod("getElementsClass");
            return (Class) getElementsClass.invoke(collectionClass);
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }
    }

}
