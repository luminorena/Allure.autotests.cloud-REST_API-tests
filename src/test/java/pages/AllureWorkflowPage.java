package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static tests.CreateUpdateTests.testCaseName;


public class AllureWorkflowPage {


    public  final SelenideElement
            userNameField = $x("//input[@name='username']"),
            passwordField = $(byName("password")),
            submitButton = $x("//button[@type='submit']");

    public final ElementsCollection createdTestCase = $$(".TreeNodeName");

    public static final String login = "allure8",
            password = "allure8";

   public void authorizeUser() {
            open("/");
            sleep(5000);
            userNameField.setValue(login).shouldBe(Condition.editable);
            sleep(5000);
            passwordField.setValue(password);
            submitButton.click();
    }

    public void openProjectPage() {
        open("/project/2230/test-cases");
    }

    public void verifyTestCaseName(){
        Selenide.refresh();
        createdTestCase.findBy(text(testCaseName))
                .shouldHave(text(testCaseName));
    }

    public void findTestCase(){
        createdTestCase.findBy(text(testCaseName)).click();
    }

}
