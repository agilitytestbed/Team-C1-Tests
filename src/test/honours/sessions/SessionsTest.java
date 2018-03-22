package honours.sessions;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Test;

import java.util.Random;

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
                post("/api/v1/sessions").
        then().assertThat().
                statusCode(200).
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
        Random r = new Random();
        RequestSpecification sessionSpec = given().header("X-session-ID", Long.toString(r.nextLong()));
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
                        endpointResponse = specification.get("/api/v1" + url);
                        break;
                    case "post":
                        endpointResponse = specification.post("/api/v1" + url);
                        break;
                    case "put":
                        endpointResponse = specification.put("/api/v1" + url);
                        break;
                    case "patch":
                        endpointResponse = specification.patch("/api/v1" + url);
                        break;
                    case "delete":
                        endpointResponse = specification.delete("/api/v1" + url);
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
