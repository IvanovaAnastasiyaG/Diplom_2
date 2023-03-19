package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.User;


public class UserClient extends BaseHttpClient{
    private static final String END_POINT_CREATE = "/api/auth/register";
    private static final String END_POINT_LOGIN = "/api/auth/login";
    private static final String END_POINT_UPDATE = "/api/auth/user";

/*    @Step("Create user")*/
    public ValidatableResponse createUser(User user) {
        return doPostRequest(END_POINT_CREATE, user);
    }

    @Step("Login user")
    public ValidatableResponse loginUser(User user) {
        return doPostRequest(END_POINT_LOGIN, user);
    }

    @Step("Change user credentials")
    public ValidatableResponse changeUsers(User user, String accessToken) {
        return doPatchRequest(END_POINT_UPDATE, user, accessToken);
    }
    @Step("Delete user")
    public ValidatableResponse deleteUser(String accessToken) {
        return doDeleteRequest(END_POINT_UPDATE, accessToken);
    }
}
