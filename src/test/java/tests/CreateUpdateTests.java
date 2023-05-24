package tests;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.github.javafaker.Faker;
import helpers.CustomApiListener;
import helpers.GenerateFakeSteps;
import models.CreateTestCaseModel;
import models.EditTestCaseModel;
import models.steps.Step;
import models.steps.StepList;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import pages.AllureWorkflowPage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static specs.RequestSpec.getLoginInSpec;
import static specs.ResponseSpec.baseTestCaseResponseSpec;

public class CreateUpdateTests extends TestBase {

    AllureWorkflowPage AllureWorkflowPage = new AllureWorkflowPage();
    public static String projectId = "2230",
            path = "/api/rs/testcasetree/leaf";
    static Faker faker = new Faker();
    public static String testCaseName = faker.name().fullName();
    String testCaseDescription = faker.lordOfTheRings().location();

    StepList stepList = new StepList();
    Step step = new Step();
    GenerateFakeSteps fakeSteps = new GenerateFakeSteps();

    @Test
    void authorize() {
        step("Авторизация", () -> {
            AllureWorkflowPage.authorizeUser();
        });
        sleep(7000);
        step("Перейти к проекту", () -> {
            AllureWorkflowPage.openProjectPage();
        });
        sleep(7000);
    }

    static String login = "allure8";
    static String password = "allure8";

    @Test
    void loginWithCookieTest() {
        step("Get cookie by api and set it to browser", () -> {
            String authorizationCookie =
                    given()
                            .filter(CustomApiListener.withCustomTemplates())
                            .contentType("application/x-www-form-urlencoded")
                            .formParam("username", login)
                            .formParam("password", password)
                    .when()
                            .post("/login")
                    .then()
                            .statusCode(302)
                            .extract()
                            .cookie("_ga_MVRXK93D28; _ga");

            step("Open minimal content", () ->
                    open("/favicon.ico"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("_ga_MVRXK93D28", authorizationCookie)));
        });

        step("Open main page", () ->
                open(""));

        step("Verify successful authorization", () ->
                $(".account").shouldHave(text(login)));
    }

    @Test
    void apiAuth() {

    }

    @Test
    void createTestCase() {
        //  step("Авторизация", this::authorize);
        step("Создать тест кейс", () -> {
            CreateTestCaseModel testCaseBody = new CreateTestCaseModel();
            testCaseBody.setName(testCaseName);
            given(getLoginInSpec())
                    .body(testCaseBody)
                    .queryParam("projectId", projectId)
            .when()
                    .post(path)
            .then()
                    .spec(baseTestCaseResponseSpec)
                    .body("statusName", is("Draft"))
                    .body("name", is(testCaseName));
        });

        step("Проверить название созданного тест кейса", () -> {
            authorize();
            sleep(3000);
            AllureWorkflowPage.verifyTestCaseName();
        });
    }

    @Test
    void createAndEditTestCase() {
        String[] testCaseId = {null};
        step("Создать тест кейс", this::createTestCase);
        step("Найти TestCaseId", () -> {
            AllureWorkflowPage.findTestCase();
            String currentUrl = getWebDriver().getCurrentUrl();
            Matcher matcher = Pattern.compile("\\d{5}+").matcher(currentUrl);
            matcher.find();
            testCaseId[0] = matcher.group();
        });
        sleep(6000);
        step("Редактировать тест кейс", () -> {
            EditTestCaseModel editTestCaseModel = new EditTestCaseModel();
            editTestCaseModel.setDescription(testCaseDescription);
            given(getLoginInSpec())
                    .body(editTestCaseModel)
            .when()
                    .patch("/api/rs/testcase/" + testCaseId[0])
            .then()
                    .spec(baseTestCaseResponseSpec)
                    .body("description", is(testCaseDescription));
        });

    }


    @Test
    void createAndAddSteps() {
        String[] testCaseId = {null};
        step("Создать тест кейс", this::createTestCase);
        step("Найти TestCaseId", () -> {
            AllureWorkflowPage.findTestCase();
            String currentUrl = getWebDriver().getCurrentUrl();
            Matcher matcher = Pattern.compile("\\d{5}+").matcher(currentUrl);
            matcher.find();
            testCaseId[0] = matcher.group();
        });
        step("Добавить шаги в тест кейс", () -> {
            String fake = fakeSteps.generateStep();
            step.setName(fake);
            stepList.setSteps(List.of(step));
            given(getLoginInSpec())
                    .body(stepList)
            .when()
                    .post("/api/rs/testcase/" + testCaseId[0] + "/scenario")
            .then()
                    .spec(baseTestCaseResponseSpec);

        });

        step("Проверить создания шага", () -> {
            open("/project/" + projectId + "/test-cases/" + testCaseId[0]);
            $(".TestCaseScenarioStep__name").shouldHave(text(step.getName()));
        });

    }

    @Test
    void createAndEditSteps() {
        String[] testCaseId = {null};
        step("Создать тест кейс с шагами", this::createAndAddSteps);
        step("Найти TestCaseId", () -> {
            AllureWorkflowPage.findTestCase();
            String currentUrl = getWebDriver().getCurrentUrl();
            Matcher matcher = Pattern.compile("\\d{5}+").matcher(currentUrl);
            matcher.find();
            testCaseId[0] = matcher.group();
        });
        step("Редактировать шаги тест кейса", () -> {
            String fake = fakeSteps.generateStep();
            step.setName(fake);
            stepList.setSteps(List.of(step));
            step.setName(faker.backToTheFuture().character());
            stepList.setSteps(List.of(step));
            given(getLoginInSpec())
                    .body(stepList)
            .when()
                    .post("/api/rs/testcase/" + testCaseId[0] + "/scenario")
            .then()
                    .spec(baseTestCaseResponseSpec);
        });

    }
}