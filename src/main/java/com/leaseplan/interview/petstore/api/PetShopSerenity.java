package com.leaseplan.interview.petstore.api;


import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

import com.leaseplan.interview.petstore.api.model.Category;
import com.leaseplan.interview.petstore.api.model.Pet;
import com.leaseplan.interview.petstore.api.model.Pet.StatusEnum;
import com.leaseplan.interview.petstore.api.model.Tag;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Random;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.RandomStringUtils;

public class PetShopSerenity {

    public PetShopSerenity() {

        RequestSpecBuilder requestSpecs = new RequestSpecBuilder();
        requestSpecs.setRelaxedHTTPSValidation();
        requestSpecs.setContentType(ContentType.JSON);
        requestSpecs.log(LogDetail.ALL);
        RestAssured.baseURI = (System.getProperty("server.host") == null) ? "https://petstore.swagger.io/v2"
            : System.getProperty("server.host");
        requestSpecification = requestSpecs.build();


    }

    @Step
    public Response whenAddPetToStore(Pet pet) {
        return given().baseUri(baseURI)
            .body(pet)
            .post("/pet").andReturn();
    }

    @Step
    public Response petIsAvailableInStoreById(long petId) {
        return given().baseUri(baseURI)
            .get("/pet/{petId}", petId)
            .andReturn();
    }

    @Step
    public Response petIsAvailableByStatus(String status) {
        return given().baseUri(baseURI)
            .queryParam("status", status)
            .get("/pet/findByStatus")
            .andReturn();
    }

    @Step
    public Response petDetailsCanBeUpdated(Pet pet) {
        return given().baseUri(baseURI)
            .body(pet)
            .put("/pet")
            .andReturn();
    }

    @Step
    public Response petCanBeRemovedFromStore(Long petId) {
        return given().baseUri(baseURI)
            .delete("/pet/" + petId)
            .andReturn();
    }

    @Step
    public Pet haveNewPet() {
        Category category = new Category();
        category.setId(new Random().nextLong());
        category.setName(RandomStringUtils.random(10, true, false));

        Tag tag = new Tag();
        tag.setId(new Random().nextLong());
        tag.setName(RandomStringUtils.random(10, true, false));

        Pet magicPet = new Pet();
        magicPet.addTagsItem(tag);
        magicPet.setName(RandomStringUtils.random(10, true, false));
        magicPet.setCategory(category);
        magicPet.setStatus(StatusEnum.AVAILABLE);
        return magicPet;

    }
}
