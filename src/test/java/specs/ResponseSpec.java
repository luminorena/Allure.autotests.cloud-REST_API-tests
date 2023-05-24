package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;

public class ResponseSpec {
    public static ResponseSpecification baseTestCaseResponseSpec =
            new ResponseSpecBuilder()
                    .log(STATUS)
                    .log(BODY)
                    .expectStatusCode(200)
                    .build();
}
