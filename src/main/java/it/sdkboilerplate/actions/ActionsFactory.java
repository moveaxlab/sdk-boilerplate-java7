package it.sdkboilerplate.actions;

import it.sdkboilerplate.exceptions.MalformedActionException;
import it.sdkboilerplate.exceptions.UndefinedActionException;
import it.sdkboilerplate.lib.ApiContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Abstract ActionsFactory class. Concrete implementations must define an hashMap between action namespaces and Action
 * subclasses. It implements a make method which returns the correct action class based on the given namespace, constructed
 * with the ApiContext
 */
public abstract class ActionsFactory {
    private ApiContext ctx;

    public abstract HashMap<String, Class<? extends Action>> getActions();

    public ActionsFactory(ApiContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Takes an action namespace as input and returns the respective Action's instance constructed with the
     * factory api context
     *
     * @param namespace Namespace of the action to be constructed
     * @return An Action's subclass instance
     * @throws UndefinedActionException if the namespace is not defined
     */
    public Action make(String namespace) throws UndefinedActionException {
        HashMap<String, Class<? extends Action>> actions = getActions();
        // If the namespace is not defined throw an exception
        if (actions.containsKey(namespace)) {
            try {
                // Otherwise get the action class related to the given namespace and its constructor(ApiContext)
                // and return a new instance with the factory api context
                Class<? extends Action> actionClass = actions.get(namespace);
                Constructor<? extends Action> constructor = actionClass.getConstructor(ApiContext.class);
                return constructor.newInstance(ctx);
            } catch (NoSuchMethodException e) {
                throw new MalformedActionException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new MalformedActionException(e.getMessage());
            } catch (InstantiationException e) {
                throw new MalformedActionException(e.getMessage());
            } catch (InvocationTargetException e) {
                throw new MalformedActionException(e.getMessage());
            }
        } else {
            throw new UndefinedActionException();
        }

    }
}
