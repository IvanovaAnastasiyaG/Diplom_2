package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Order;

public class OrderClient extends BaseHttpClient{
    private final static String END_POINT_CREATE = "/api/orders";

    @Step("Create order")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return doPostRequest(END_POINT_CREATE, order, accessToken);
    }

    @Step("Get list of orders")
    public ValidatableResponse getOrders(String accessToken) {
        return doGetRequest(END_POINT_CREATE, accessToken);
    }
}
