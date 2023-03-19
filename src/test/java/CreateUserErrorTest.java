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

import static model.Error.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserErrorTest {
    User user;
    String message;
    String accessToken;
    UserClient userClient = new UserClient();

    public CreateUserErrorTest(User user, String message) {
        this.user = user;
        this.message = message;
    }

    @Before
    public void createUser() {
        User user = new User("email_data@yandex.ru", "password", "UserTest");
        accessToken = userClient.loginUser(user).extract().path("accessToken");
        if (accessToken == null){
            accessToken = userClient.createUser(user).extract().path("accessToken");
        }
    }

    @Parameterized.Parameters(name = "Создание пользователя. Тестовые данные: {0} {1} {2} {3}")
    public static Object[][] createUserData() {
        return new Object[][]{
                {new User("email_data@yandex.ru", "password", "UserTest"),MESSAGE_USER_ALREADY_EXISTS},
                {new User("testdata@yandex.ru", "password", null), MESSAGE_MISSING_REQUIRED_FIELDS},
                {new User("testdata@yandex.ru", null,"Username"), MESSAGE_MISSING_REQUIRED_FIELDS},
                {new User(null, "password","Username"), MESSAGE_MISSING_REQUIRED_FIELDS},
        };
    }

    @Test
    @DisplayName("Check there is 403 error and error message when trying to create a exist user or user without required field")
    public void createUserExistsCannotBeCreated() {
        ValidatableResponse response = userClient.createUser(user);
        int status = response.extract().statusCode();
        String error = response.extract().path("message");
        assertThat("Status code is 403", status, equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat("Error bad user data", message, equalTo(error));
    }
    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}
