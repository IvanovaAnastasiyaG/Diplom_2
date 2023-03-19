import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserTest {
    User user;
    String accessToken;
    UserClient userClient = new UserClient();

    public CreateUserTest(User user) {
        this.user = user;
    }

    @Parameterized.Parameters(name = "Создание пользователя. Тестовые данные: {0} {1} {2}")
    public static Object[][] createUserData() {
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
    @DisplayName("Check a unique user can be created")
    public void createUniqueUserCanBeCreated() {
        ValidatableResponse response = userClient.createUser(user);
        int status = response.extract().statusCode();
        boolean success = response.extract().path("success");
        accessToken = response.extract().path("accessToken");
        assertThat("Status code is 200", status, equalTo(HttpStatus.SC_OK));
        assertThat("Create user is success", true, equalTo(success));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}
