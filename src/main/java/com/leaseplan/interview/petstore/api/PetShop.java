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
import org.apache.commons.lang3.RandomStringUtils;

public class PetShop {

    public PetShop() {

        RequestSpecBuilder requestSpecs = new RequestSpecBuilder();
        requestSpecs.setRelaxedHTTPSValidation();
        requestSpecs.setContentType(ContentType.JSON);
        requestSpecs.log(LogDetail.ALL);
        RestAssured.baseURI = (System.getProperty("server.host") == null) ? "https://petstore.swagger.io/v2"
            : System.getProperty("server.host");
        requestSpecification = requestSpecs.build();


    }

    public Response addNewPetToStoreReturnResponse(Pet pet) {
        return given().baseUri(baseURI)
            .body(pet)
            .post("/pet").andReturn();
    }

    public Response getPetByIDreturnResponse(long petId) {
        return given().baseUri(baseURI)
            .get("/pet/{petId}", petId)
            .andReturn();
    }

    public Response getPetByStatusReturnResponse(String status) {
        return given().baseUri(baseURI)
            .queryParam("status", status)
            .get("/pet/findByStatus")
            .andReturn();
    }

    public Response updatePet(Pet pet) {
        return given().baseUri(baseURI)
            .body(pet)
            .put("/pet")
            .andReturn();
    }

    public Response deletePet(Long petId) {
        return given().baseUri(baseURI)
            .delete("/pet/" + petId)
            .andReturn();
    }

    public Pet buildPet() {
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
