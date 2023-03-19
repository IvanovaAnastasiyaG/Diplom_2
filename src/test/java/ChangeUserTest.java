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

import static model.Error.MESSAGE_USER_SHOULD_BE_AUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class ChangeUserTest {
    User user;
    String accessToken;
    UserClient userClient = new UserClient();

    public ChangeUserTest(User user) {
        this.user = user;
    }

    @Before
    public void createUser() {
        User user = new User("email_data@yandex.ru", "password", "Username");
        accessToken = userClient.loginUser(user).extract().path("accessToken");
        if (accessToken == null){
            accessToken = userClient.createUser(user).extract().path("accessToken");
        }
    }

    @Parameterized.Parameters(name = "Изменение пользователя. Тестовые данные: {0} {1} {2}")
    public static Object[][] changeUserData() {
        return new Object[][]{
                {new User("testemailaddres@yandex.ru", "password", "Username")},
                {new User("testEmailAddres@yandex.ru", "PassWord", "Анастасия")},
                {new User("TESTEMAIL@yandex.ru", "PASSWORD", "Username Test")},
                {new User("test_email_addres@yandex.ru", "password56", "Анастасия Иванова")},
                {new User("testemailaddres10@yandex.ru", "!password", "Иванова Анастасия Григорьевна")},
                {new User("testemailaddres@yandex100.ru", "!password45", "ИВАНОВА АНАСТАСИЯ ГРИГОРЬЕВНА")},
                {new User("test-email@yandex.ru", "passwordpasswordpassword", "JOHN SMITH")},
                {new User("testemail@ya-ndex.ru", "pass_word", "Zvezda35")},
                {new User("test.email@yandex.ru", "pass word", "$Magnat$")},
                {new User("testemail@test.yandex.ru", "@pa!ss_word!", "John Smith")},
        };
    }

    @Test
    @DisplayName("Check users data can be changed with authorization")
    public void changeUsersDataWithAuth() {
        ValidatableResponse response = userClient.changeUsers(user, accessToken);
        int status = response.extract().statusCode();
        boolean success = response.extract().path("success");
        assertThat("Status code is 200", status, equalTo(HttpStatus.SC_OK));
        assertThat("Create user is success", true, equalTo(success));
    }

    @Test
    @DisplayName("Check users data cannot be changed without authorization")
    public void changeUsersDataWithoutAuth() {
        ValidatableResponse response = userClient.changeUsers(user, "");
        int status = response.extract().statusCode();
        String error = response.extract().path("message");
        assertThat("Status code is 403", status, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat("Error: user should be authorized", MESSAGE_USER_SHOULD_BE_AUTHORIZED, equalTo(error));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}
