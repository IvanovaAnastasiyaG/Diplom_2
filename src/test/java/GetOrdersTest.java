import client.OrderClient;
import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static model.Error.MESSAGE_USER_SHOULD_BE_AUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {
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
    @DisplayName("Check there is list of orders when user is authorized")
    public void lstOfUserOrdersWithAuth() {
        Order order = new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa79"));
        orderClient.createOrder(order, accessToken);
        ValidatableResponse response = orderClient.getOrders(accessToken);
        int status = response.extract().statusCode();
        assertThat("Status code is 200", status, equalTo(HttpStatus.SC_OK));
        assertThat("Order list is not null", response.extract().path("orders"), notNullValue());
    }

    @Test
    @DisplayName("Check there is no list of orders when user is not authorized")
    public void lstOfUserOrdersWithWrongAuth() {
        accessToken = "wrongToken";
        ValidatableResponse response = orderClient.getOrders(accessToken);
        int status = response.extract().statusCode();
        String error = response.extract().path("message");
        assertThat("Status code is 401", status, equalTo(HttpStatus.SC_UNAUTHORIZED));
        assertThat("Error - missing ingredient(s)", MESSAGE_USER_SHOULD_BE_AUTHORIZED, equalTo(error));
    }
}
