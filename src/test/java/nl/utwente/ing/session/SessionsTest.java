package nl.utwente.ing.session;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nl.utwente.ing.Utils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.Assert.fail;

public class SessionsTest {

    private static final String[][] securedEndpoints = new String[][] {
            {"get,post", "/transactions"}, {"get,put,delete", "/transactions/{transactionid}"},
            {"patch", "/transactions/{transactionId}/category"}, {"get,post", "/categories"},
            {"get,put,delete", "/categories/{categoryId}"}
    };

    @Test
    public void getSession() {
        when().
                post(Utils.BASE_URL + "/sessions").
        then().assertThat().
                statusCode(201).
                contentType("application/json").
                body(matchesJsonSchemaInClasspath("session-response.json"));
    }

    @Test
    public void useNoSession() {
        RequestSpecification emptySpec = given();
        sendEndpointRequests(emptySpec);
    }

    @Test
    public void useInvalidSession() {
        RequestSpecification sessionSpec = given().header("X-session-ID", "Invalid_Session_ID");
        sendEndpointRequests(sessionSpec);
    }

    private void sendEndpointRequests(RequestSpecification specification) {
        for (String[] endpoint : securedEndpoints) {
            String[] methods = endpoint[0].split(",");
            String url = endpoint[1].replaceAll("\\{.*}", "1");
            for (String method : methods) {
                Response endpointResponse;
                switch (method) {
                    case "get":
                        endpointResponse = specification.get(Utils.BASE_URL + url);
                        break;
                    case "post":
                        endpointResponse = specification.post(Utils.BASE_URL + url);
                        break;
                    case "put":
                        endpointResponse = specification.put(Utils.BASE_URL + url);
                        break;
                    case "patch":
                        endpointResponse = specification.patch(Utils.BASE_URL + url);
                        break;
                    case "delete":
                        endpointResponse = specification.delete(Utils.BASE_URL + url);
                        break;
                    default:
                        return;
                }
                try {
                    endpointResponse.then().assertThat().statusCode(401);
                } catch (AssertionError e) {
                    fail(String.format("%s: %s\n%s", method.toUpperCase(), url, e.getMessage().split("\n")[1]));
                }

            }
        }
    }

}
