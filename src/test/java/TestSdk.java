import it.sdkboilerplate.actions.ActionsFactory;
import it.sdkboilerplate.exceptions.*;
import it.sdkboilerplate.http.Headers;
import it.sdkboilerplate.http.MediaType;
import it.sdkboilerplate.http.SdkRequest;
import it.sdkboilerplate.http.SdkResponse;
import it.sdkboilerplate.http.agents.ApacheHttpAgent;
import it.sdkboilerplate.lib.ApiContext;
import it.sdkboilerplate.lib.FormSerializer;
import it.sdkboilerplate.lib.JsonDeserializer;
import it.sdkboilerplate.lib.JsonSerializer;
import mocks.actions.TestActionFactory;
import mocks.actions.TestCreateUserAction;
import mocks.actions.TestRetrieveUserAction;
import mocks.exceptions.ValidationException;
import mocks.objects.*;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "FieldCanBeLocal"})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
public class TestSdk {
    private ActionsFactory actionsFactory;
    private ApiContext ctx;

    public HashMap<String, String> getDefaultRequestHeaders() {
        return defaultRequestHeaders;
    }

    public HashMap<String, String> getDefaultResponseHeaders() {
        return defaultResponseHeaders;
    }

    private HashMap<String, String> defaultRequestHeaders = new HashMap();
    private HashMap<String, String> defaultResponseHeaders = new HashMap();

    private final String userRetrievalJsonSerialization = "{\"accounts\":[{\"name\": \"testAccount\"}]," +
            "\"contact\":{\"type\":\"email\",\"value\":\"testEmail\"}," +
            "\"name\":\"testName\"," +
            "\"surname\":\"testSurname\"}";

    private final String userCreationJsonSerialization = "{\"name\":\"testName\",\"surname\":\"testSurname\"}";

    public TestSdk() throws ConfigurationException {
        HashMap<String, Object> config = new HashMap();
        config.put("timeout", 1000);
        config.put("mode", "live");
        config.put("verifySSL", false);
        this.ctx = new ApiContext("http://testhostname.com", config, null);
        actionsFactory = new TestActionFactory(this.ctx);
        this.defaultRequestHeaders.put(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        this.defaultResponseHeaders.put(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        this.defaultRequestHeaders.put(Headers.ACCEPT, MediaType.APPLICATION_JSON);
        this.defaultResponseHeaders.put(Headers.ACCEPT, MediaType.APPLICATION_JSON);
    }

    /**
     * Tests the correct action instantiation from the factory method
     */
    @Test

    public void testActionsFactory() throws UndefinedActionException {

        Object actionRetrieval = actionsFactory.make("testUserRetrieval");
        Assert.assertTrue(actionRetrieval instanceof TestRetrieveUserAction);
        Assert.assertEquals(((TestRetrieveUserAction) actionRetrieval).getRoute(), "/read/{userUUID}");
        Assert.assertEquals(((TestRetrieveUserAction) actionRetrieval).getVerb(), "GET");

        Object actionCreation = actionsFactory.make("testUserCreation");
        Assert.assertTrue(actionCreation instanceof TestCreateUserAction);
        Assert.assertEquals(((TestCreateUserAction) actionCreation).getRoute(), "/create");
        Assert.assertEquals(((TestCreateUserAction) actionCreation).getVerb(), "POST");


    }

    /**
     * Tests the correct exception raising from the factory method when trying to contruct a non-existent action
     */
    @Test(expected = UndefinedActionException.class)
    public void testActionFactoryErrors() throws UndefinedActionException {
        actionsFactory.make("__NonExistentAction");
    }

    /**
     * Test the HashMap sdkObject serialization
     */
    @Test
    public void testObjectToHashMap() {
        TestUserCreation userCreation = new TestUserCreation("testName", "testSurname");
        HashMap<String, Object> serialized = userCreation.toHashMap();
        Assert.assertEquals(serialized.get("name"), "testName");
        Assert.assertEquals(serialized.get("surname"), "testSurname");

        ArrayList<TestAccount> accounts = new ArrayList();
        accounts.add(new TestAccount("testAccount"));
        TestAccountsCollection accountsCollection = new TestAccountsCollection(accounts);

        TestContactRetrieval contact = new TestContactRetrieval("email", "testEmail");
        TestUserRetrieval userRetrieval = new TestUserRetrieval("testName", "testSurname", contact, accountsCollection);

        HashMap<String, Object> userRetrievalSerialized = userRetrieval.toHashMap();
        HashMap<String, Object> serializedContact = (HashMap<String, Object>) userRetrievalSerialized.get("contact");
        Assert.assertEquals(serializedContact.get("type"), "email");
        Assert.assertEquals(serializedContact.get("value"), "testEmail");

        ArrayList<HashMap<String, Object>> serializedAccounts = (ArrayList<HashMap<String, Object>>) userRetrievalSerialized.get("accounts");
        Assert.assertEquals(serializedAccounts.size(), 1);
        HashMap<String, Object> serializedAccount = serializedAccounts.get(0);
        Assert.assertEquals(serializedAccount.get("name"), "testAccount");


    }

    /**
     * Tests Objects Json Serialization
     */
    @Test
    public void testObjectToJson() throws UnserializableObjectException {
        TestUserCreation userCreation = new TestUserCreation("testName", "testSurname");
        JsonSerializer serializer = new JsonSerializer();

        String userCreationSerialized = serializer.serialize(userCreation);

        char[] sortedUser = userCreationSerialized.toCharArray();
        char[] sortedCheckUser = this.userCreationJsonSerialization.toCharArray();

        Arrays.sort(sortedUser);
        Arrays.sort(sortedCheckUser);
        //TODO SORT THE STRINGS USING 1.6 METHODS
        Assert.assertEquals(new String(sortedUser), new String(sortedCheckUser));
        ArrayList<TestAccount> accounts = new ArrayList();
        accounts.add(new TestAccount("testAccount"));
        TestAccountsCollection accountsCollection = new TestAccountsCollection(accounts);

        TestContactRetrieval contact = new TestContactRetrieval("email", "testEmail");
        TestUserRetrieval userRetrieval = new TestUserRetrieval("testName", "testSurname", contact, accountsCollection);
        String userRetrievalSerialized = serializer.serialize(userRetrieval);

        char[] sortedUserRetrieval = userRetrievalSerialized.toCharArray();
        char[] sortedCheckUserRetrieval = this.userRetrievalJsonSerialization.toCharArray();

        Arrays.sort(sortedUserRetrieval);
        Arrays.sort(sortedCheckUserRetrieval);
        Assert.assertEquals(
                new String(sortedUserRetrieval).replace(" ", ""),
                new String(sortedCheckUserRetrieval).replace(" ", ""));
    }

    /**
     * Tests Objects Form Serialization
     */
    @Test
    public void testObjectToForm() throws UnserializableObjectException {
        TestUserCreation userCreation = new TestUserCreation("testName", "testSurname");
        FormSerializer serializer = new FormSerializer();

        String userCreationSerialized = serializer.serialize(userCreation);
        Assert.assertEquals(userCreationSerialized, "name=testName&surname=testSurname");
    }

    /**
     * Tests Objects json De-Serialization
     */
    @Test
    public void testJsonToObject() throws UnknownBodyTypeException, DeserializationException {
        JsonDeserializer jsonDeserializer = new JsonDeserializer();

        TestUserRetrieval userRetrieval = (TestUserRetrieval) jsonDeserializer.deserialize(this.userRetrievalJsonSerialization, TestUserRetrieval.class);
        this.checkUserRetrieval(userRetrieval);
    }

    /**
     * Tests Response Body Formatting
     */
    @Test
    public void testResponseBodyJsonFormatting() throws UnknownContentTypeException, DeserializationException, UnknownBodyTypeException {
        HashMap<String, String> headers = new HashMap();
        headers.put(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        SdkResponse response = new SdkResponse(200, this.userRetrievalJsonSerialization, headers);
        TestUserRetrieval userRetrieval = response.format(TestUserRetrieval.class);
        this.checkUserRetrieval(userRetrieval);


    }

    /**
     * Tests Response Body Form Formatting
     */
    @Test
    public void testResponseBodyFormFormatting() throws UnknownContentTypeException, DeserializationException, UnknownBodyTypeException {
        HashMap<String, String> headers = new HashMap();
        headers.put(Headers.CONTENT_TYPE, MediaType.APPLICATION_FORM);
        String userFormSerialization = "name=testName&surname=testSurname";
        SdkResponse response = new SdkResponse(200, userFormSerialization, headers);
        TestUserCreation userCreation = response.format(TestUserCreation.class);
        this.checkUserCreation(userCreation);
    }

    private void checkUserRetrieval(TestUserRetrieval userRetrieval) {
        Assert.assertEquals(userRetrieval.getName(), "testName");
        Assert.assertEquals(userRetrieval.getSurname(), "testSurname");
        Assert.assertEquals(userRetrieval.getContact().getType(), "email");
        Assert.assertEquals(userRetrieval.getContact().getValue(), "testEmail");

        TestAccountsCollection accountsCollection = userRetrieval.getAccounts();
        Assert.assertEquals(accountsCollection.getSize(), (Integer) 1);

        TestAccount testAccount = accountsCollection.getElements().get(0);
        Assert.assertEquals(testAccount.getName(), "testAccount");
    }

    private void checkUserCreation(TestUserCreation userCreation) {
        Assert.assertEquals(userCreation.getName(), "testName");
        Assert.assertEquals(userCreation.getSurname(), "testSurname");
    }

    @Test
    public void testActionRouteBuilding() throws Exception {
        TestRetrieveUserAction testRetrieveUserAction = (TestRetrieveUserAction) this.actionsFactory.make("testUserRetrieval");
        testRetrieveUserAction.setRouteParameter("userUUID", "abc");
        String route = Whitebox.invokeMethod(testRetrieveUserAction, "buildRoute");
        Assert.assertEquals(route, "/read/abc");
    }

    /**
     * Tests Action run
     */
    @Test
    @PrepareForTest({ApacheHttpAgent.class, TestCreateUserAction.class})
    public void testActionRun() throws Exception {

        TestCreateUserAction testCreateUser = this.setupUserCreationAction();
        SdkResponse testResponse = new SdkResponse(201, null, this.getDefaultResponseHeaders());
        ApacheHttpAgent mockAgent = PowerMockito.spy(new ApacheHttpAgent(this.ctx.getHostname(), this.ctx.getConfig()));

        PowerMockito.doReturn(testResponse).when(mockAgent, "send", any());
        PowerMockito.doReturn(mockAgent).when(testCreateUser, "getUserAgent");
        Object formattedResponse = testCreateUser.run();
        Assert.assertNull(formattedResponse);
        verify(mockAgent, times(1)).send(any(SdkRequest.class));
        PowerMockito.verifyPrivate(testCreateUser, times(1)).invoke("runPreSendHooks", any(SdkRequest.class));
        PowerMockito.verifyPrivate(testCreateUser, times(1)).invoke("runSuccessHooks", any(SdkRequest.class), any(SdkResponse.class));
        PowerMockito.verifyPrivate(testCreateUser, times(0)).invoke("runFailureHooks", any(SdkRequest.class), any(SdkResponse.class), any(SdkHttpException.class));


    }

    /**
     * Tests action run with Failure responses
     */
    @Test(expected = ValidationException.class)
    @PrepareForTest({ApacheHttpAgent.class, TestCreateUserAction.class})
    public void testActionFailureRun() throws Exception {
        TestCreateUserAction testCreateUser = this.setupUserCreationAction();

        SdkResponse testResponse = new SdkResponse(422, null, this.getDefaultResponseHeaders());
        ApacheHttpAgent mockAgent = PowerMockito.spy(new ApacheHttpAgent(this.ctx.getHostname(), this.ctx.getConfig()));
        PowerMockito.doReturn(testResponse).when(mockAgent, "send", any());
        PowerMockito.doReturn(mockAgent).when(testCreateUser, "getUserAgent");
        try {
            testCreateUser.run();
        } catch (ValidationException e) {
            PowerMockito.verifyPrivate(testCreateUser, times(0)).invoke("runSuccessHooks", any(SdkRequest.class), any(SdkResponse.class));
            PowerMockito.verifyPrivate(testCreateUser, times(1)).invoke("runFailureHooks", any(SdkRequest.class), any(SdkResponse.class), any(ValidationException.class));
            throw e;
        }

    }

    private TestCreateUserAction setupUserCreationAction() throws Exception {
        TestCreateUserAction testCreateUser = PowerMockito.spy((TestCreateUserAction) this.actionsFactory.make("testUserCreation"));
        TestUserCreation testUserCreation = new TestUserCreation("testName", "testSurname");
        testCreateUser.setRequestBody(testUserCreation);
        return testCreateUser;
    }

    private TestRetrieveUserAction setupUserRetrievalAction() throws Exception {
        return PowerMockito.spy((TestRetrieveUserAction) this.actionsFactory.make("testUserRetrieval"));

    }

    /**
     * Tests OkHttpClient User Agent send method
     */
    @Test
    @PrepareForTest(ApacheHttpAgent.class)
    public void testUserAgentSend() throws Exception {
        ApacheHttpAgent mockAgent = PowerMockito.spy(new ApacheHttpAgent(this.ctx.getHostname(), this.ctx.getConfig()));
        SdkRequest request = new SdkRequest("/testRoute", "POST", this.getDefaultRequestHeaders(), new HashMap(), this.userCreationJsonSerialization);
        /*
        TODO test internal agents methods call
         */


    }

}
