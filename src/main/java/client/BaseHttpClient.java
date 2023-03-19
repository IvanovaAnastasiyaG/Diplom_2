package client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseHttpClient {
    protected final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URL)
                .build();
    }

    protected ValidatableResponse doGetRequest(String uri, String accessToken) {
        return given()
                .spec(baseSpec())
                .header("Authorization", accessToken)
                .get(uri)
                .then();
    }

    protected ValidatableResponse doPostRequest(String uri, Object body) {
        return given()
                .spec(baseSpec())
                .body(body)
                .post(uri)
                .then();
    }

    protected ValidatableResponse doPostRequest(String uri, Object body, String accessToken) {
        return given()
                .spec(baseSpec())
                .header("Authorization", accessToken)
                .body(body)
                .post(uri)
                .then();
    }

    protected ValidatableResponse doPatchRequest(String uri, Object body, String accessToken) {
        return given()
                .spec(baseSpec())
                .header("Authorization", accessToken)
                .body(body)
                .patch(uri)
                .then();
    }

    protected ValidatableResponse doDeleteRequest(String uri, String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .spec(baseSpec())
                .delete(uri)
                .then();
    }
}
