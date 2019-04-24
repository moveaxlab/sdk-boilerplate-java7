package it.sdkboilerplate.objects;

import it.sdkboilerplate.exceptions.UnserializableObjectException;

import java.util.ArrayList;


@SuppressWarnings("unchecked")
public abstract class SdkCollection<E> extends SdkBodyType {
    private ArrayList<E> elements;

    public static Class<? extends SdkBodyType> getElementsClass() {
        return null;
    }

    public ArrayList<E> getElements() {
        return this.elements;
    }

    public Integer getSize() {
        return this.elements.size();
    }

    public ArrayList<E> toArrayList() throws UnserializableObjectException {
        ArrayList<E> elements = new ArrayList();
        for (E element : this.elements) {
            if (element instanceof SdkObject) {
                elements.add((E) ((SdkObject) element).toHashMap());
            } else if (element instanceof SdkCollection) {
                elements.add((E) ((SdkCollection) element).toArrayList());
            } else {
                elements.add(element);
            }
        }
        return elements;
    }


    public SdkCollection(ArrayList<E> elements) {
        this.elements = elements;
    }

    public SdkCollection() {
    }
}
