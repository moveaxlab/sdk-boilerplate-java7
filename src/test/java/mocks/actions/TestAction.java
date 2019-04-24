package mocks.actions;

import it.sdkboilerplate.actions.Action;
import it.sdkboilerplate.hooks.FailureHook;
import it.sdkboilerplate.hooks.PreSendHook;
import it.sdkboilerplate.hooks.SuccessHook;
import it.sdkboilerplate.lib.ApiContext;

import java.util.ArrayList;

public abstract class TestAction extends Action {
    public TestAction(ApiContext ctx) {
        super(ctx);
    }

    @Override
    public ArrayList<Class<? extends FailureHook>> getFailureHooks() {
        return new ArrayList();
    }

    @Override
    public ArrayList<Class<? extends SuccessHook>> getSuccessHooks() {
        return new ArrayList();
    }

    @Override
    public ArrayList<Class<? extends PreSendHook>> getPreSendHooks() {
        return new ArrayList();
    }

}
