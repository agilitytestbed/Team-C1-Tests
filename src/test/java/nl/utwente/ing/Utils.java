package nl.utwente.ing;

import static io.restassured.RestAssured.when;

public class Utils {
    public static final String BASE_URL = "/api/v2";
    public static final String CATEGORIES_PATH = "/categories";
    public static final String TRANSACTIONS_PATH = "/transactions";

    public static final String X_SESSION_ID_HEADER = "X-Session-ID";
    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static final String EMPTY_BODY = "";


    public static String getValidSessionId() {
        return when().
                post(BASE_URL + "/sessions").
                then().assertThat().
                statusCode(201).
                contentType(Utils.APPLICATION_JSON_VALUE).
                extract().
                path("id");
    }
}
