import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static model.Error.MESSAGE_INCORRECT_USER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class LoginUserErrorTest {
    User user;
    String accessToken;
    UserClient userClient = new UserClient();

    public LoginUserErrorTest(User user) {
        this.user = user;
    }
    @Before
    public void createUser() {
        User user = new User("test_email@yandex.ru", "password", "Username");
        accessToken = userClient.createUser(user).extract().path("accessToken");
    }

    @Parameterized.Parameters(name = "Авторизация пользователя. Тестовые данные: {0} {1} {2}")
    public static Object[][] createUserData() {
        return new Object[][]{
                {new User("11test-data@yandex.ru", "password", "Username")},
                {new User("test_email@yandex.ru", "pass1word", "Username")},
                {new User(null, "password", "Username")},
                {new User(null, "password", null)},
                {new User("test_email@yandex.ru", null, "Username")},
                {new User("test_email@yandex.ru", null, null)},
        };
    }

    @Test
    @DisplayName("Check a user with wrong login and password cannot login")
    public void loginUserWithWrongLoginOrPassword() {
        ValidatableResponse responseToLogin = userClient.loginUser(user);
        int status = responseToLogin.extract().statusCode();
        String error = responseToLogin.extract().path("message");
        assertThat("Status code is 401", status, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat("Error - user already exist", MESSAGE_INCORRECT_USER, equalTo(error));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}
