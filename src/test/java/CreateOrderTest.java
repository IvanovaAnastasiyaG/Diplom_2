import client.OrderClient;
import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import model.Error;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static model.Error.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    private String accessToken;
    OrderClient orderClient = new OrderClient();
    UserClient userClient = new UserClient();

    @Before
    public void createUser() {
        User user = new User("testdata@yandex.ru", "password", "Username");
        accessToken = userClient.loginUser(user).extract().path("accessToken");
        if (accessToken == null){
            accessToken = userClient.createUser(user).extract().path("accessToken");
        }
    }

    @Test
    @DisplayName("Check order creation with auth and ingredients")
    public void checkOrderCreationWithAuth() {
        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa79"));
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        int status = response.extract().statusCode();
        boolean success = response.extract().path("success");
        assertThat("Status code is 200", status, equalTo(HttpStatus.SC_OK));
        assertThat("Create order is success", true, equalTo(success));
    }

    @Test
    @DisplayName("Check order can be created without auth and with ingredients")
    public void checkOrderCannotBeCreatedWithoutAuth() {
        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa79"));
        ValidatableResponse response = orderClient.createOrder(order, "");
        int status = response.extract().statusCode();
        boolean success = response.extract().path("success");
        assertThat("Status code is 200", status, equalTo(HttpStatus.SC_OK));
        assertThat("Create order is success", true, equalTo(success));
    }

    @Test
    @DisplayName("Check order cannot be created created with authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutIngredients() {
        Order order = new Order();
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        int status = response.extract().statusCode();
        String error = response.extract().path("message");
        assertThat("Status code is 400", status, equalTo(HttpStatus.SC_BAD_REQUEST));
        assertThat("Error - missing ingredient(s)", error, equalTo(MESSAGE_MISSING_INGREDIENT));
    }

    @Test
    @DisplayName("Check order cannot be created created without authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutAuthAndIngredients() {
        Order order = new Order();
        ValidatableResponse response = orderClient.createOrder(order, "");
        int status = response.extract().statusCode();
        String error = response.extract().path("message");
        assertThat("Status code is 400", status, equalTo(HttpStatus.SC_BAD_REQUEST));
        assertThat("Error - user should be authorized", error, equalTo(MESSAGE_MISSING_INGREDIENT));
    }

    @Test
    @DisplayName("Check order cannot be created with wrong ingredients hash")
    public void checkOrderCannotBeCreatedWithWrongIngredientHash() {
        Order order = new Order(Arrays.asList("!@61c0c5a71d1f82001bdaaa6d", "$61c0c5a71d1f82001bdaaa6d%", "#61c0c5a71d1f82001bdaaa6d$"));
        ValidatableResponse response = orderClient.createOrder(order, accessToken);
        int status = response.extract().statusCode();
        assertThat("Status code is 500", status, equalTo(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser(accessToken);
    }
}


