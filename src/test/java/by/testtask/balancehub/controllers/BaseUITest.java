package by.testtask.balancehub.controllers;

import by.testtask.balancehub.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static by.testtask.balancehub.utils.Constants.TOKEN_HEADER;
import static by.testtask.balancehub.utils.Constants.TOKEN_TYPE;
import static by.testtask.balancehub.utils.TestConstants.*;
import static io.restassured.RestAssured.given;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BaseUITest extends BaseTest {
    protected static RequestSpecification requestSpecification;

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUp() {
        requestSpecification = RestAssured.given()
                .baseUri(String.format(BASE_URL, port))
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);
    }

    protected ValidatableResponse checkStatusCodeAndBodyInGetRequest(String url, int code, String schema,
                                                                     Object requestBody, String validUserDataJson) {
        String accessToken = getAccessToken(validUserDataJson);
        return RestAssured.given(requestSpecification)
                .header(TOKEN_HEADER, TOKEN_TYPE + accessToken)
                .body(requestBody)
                .port(DEFAULT_APP_PORT)
                .get(url)
                .then()
                .statusCode(code)
                .body(matchesJsonSchemaInClasspath(schema))
                .time(lessThan(DEFAULT_TIMEOUT));
    }

    protected String getAccessToken(String validUserDataJson) {
        return given(requestSpecification)
                .body(validUserDataJson)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response()
                .jsonPath()
                .getString("accessToken");
    }

    @Test
    public void baseTest() {
        String accessToken = getAccessToken(USER_CRED);
        assertFalse("Access token should not be empty", accessToken.isEmpty());
    }

}
