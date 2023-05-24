package specs;


import auth.Auth;
import config.UserDataConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.Cookie;


import static auth.Auth.ALLURE_TESTOPS_SESSION;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static helpers.CustomApiListener.withCustomTemplates;
import static tests.TestBase.userDataConfig;



public class RequestSpec {
   // public static RequestSpecification baseUserRequestSpec = with()
          /*  .filter(CustomApiListener.withCustomTemplates())
            .header("X-XSRF-TOKEN", "6898802d-8a38-4cd3-a9c1-435908222a8e")
            .cookies("XSRF-TOKEN", "6898802d-8a38-4cd3-a9c1-435908222a8e",
                    "ALLURE_TESTOPS_SESSION", "cade305a-1006-400a-86ec-4252b7ca2404")
            .contentType("application/json;charset=UTF-8")
            .log().all()
            .contentType(JSON);*/


    public final static String
            user = userDataConfig.username(),
            password = userDataConfig.password(),
            userToken = userDataConfig.token();

    public static RequestSpecification getLoginInSpec() {

        Auth authApi = new Auth();
        String xsrfToken = authApi.getXsrfToken(userToken);
        String authorizationCookie = authApi
                .getAuthorizationCookie(userToken, user, password);

        return RestAssured
                .given()
                .log().all()
                .filter(withCustomTemplates())
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookies("XSRF-TOKEN", xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .contentType(ContentType.JSON);

    }

    public static void setCookies() {
        String authorizationCookie = new Auth()
                .getAuthorizationCookie(userToken, user, password);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));
    }
}
