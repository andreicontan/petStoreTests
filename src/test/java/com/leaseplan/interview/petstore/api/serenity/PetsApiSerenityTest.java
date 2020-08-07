package com.leaseplan.interview.petstore.api.serenity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.leaseplan.interview.petstore.api.PetShopSerenity;
import com.leaseplan.interview.petstore.api.model.Pet;
import com.leaseplan.interview.petstore.api.model.Pet.StatusEnum;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class PetsApiSerenityTest {



    @Steps
    private PetShopSerenity petShop;
    private Long petId;

    @Before
    public void init() {
        petShop = new PetShopSerenity();
    }

    @Test
    public void addNewPetToStoreReturnsPet() {
        //Arrange
        Pet newPet = petShop.haveNewPet();
        //Act
        Pet postResponseBody = petShop.whenAddPetToStore(newPet)
            .then()
            .extract()
            .body().as(Pet.class);
        petId = postResponseBody.getId();

        //Assert
        assertNotNull(postResponseBody.getId());
        newPet.setId(postResponseBody.getId());
        assertEquals(newPet, postResponseBody);
    }

    @Test
    @Ignore("Should return 405 according to swagger file")
    public void addNewPetToStoreInvalidInput_returns405() {

        petId = petShop.whenAddPetToStore(new Pet())
            .then()
            .statusCode(HttpStatus.SC_METHOD_NOT_ALLOWED)
            .extract()
            .body()
            .as(Pet.class).getId();
    }

    @Test
    public void getPetByIdReturnsPet() {
        //Arrange
        Pet newPet = petShop.haveNewPet();
        Pet petPostResponseBody = petShop.whenAddPetToStore(newPet)
            .then()
            .extract()
            .body().as(Pet.class);
        petId = petPostResponseBody.getId();

        //Act
        Pet petGetResponseBody = petShop.petIsAvailableInStoreById(petPostResponseBody.getId())
            .then()
            .extract()
            .body()
            .as(Pet.class);

        //Assert
        assertEquals(petPostResponseBody, petGetResponseBody);
    }

    @Test
    public void getPetByInvalidStatus_returnsEmptyList() {
        Pet newPet = petShop.haveNewPet();

        petId = petShop.whenAddPetToStore(newPet)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(Pet.class).getId();
        ;

        assertEquals(petShop.petIsAvailableByStatus("invalidStatus")
            .as(List.class).size(), 0);
    }

    @Test
    public void getPetByStatus_returnsList() {
        Pet newPet = petShop.haveNewPet();
        Pet petPostResponseBody = petShop.whenAddPetToStore(newPet)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(Pet.class);
        petId = petPostResponseBody.getId();
        final StatusEnum petStatus = petPostResponseBody.getStatus();

        List<Pet> petsByStatusBody = Arrays.asList(petShop.petIsAvailableByStatus(petStatus.getValue())
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract()
            .body()
            .as(Pet[].class));

        assertTrue(petsByStatusBody.size() > 0);

        List<Pet> filteredPetsByStatus = petsByStatusBody.stream().filter(pet -> pet.getStatus().equals(petStatus))
            .collect(Collectors.toList());

        assertEquals(petsByStatusBody, filteredPetsByStatus);
    }

    @Test
    //It's supposed to fail due to inconsistency of data between the 2 objects
    public void updateNameExistingPet_ReturnsOK() {
        //Arrange
        Pet newPet = petShop.haveNewPet();
        Pet petPostResponseBody = petShop.whenAddPetToStore(newPet).
            then().
            statusCode(HttpStatus.SC_OK)
            .extract().body().as(Pet.class);
        petId = petPostResponseBody.getId();
        petPostResponseBody.setName("petNewName");
        petPostResponseBody.addPhotoUrlsItem("newPhotoUrl");

        //Act
        Pet updatedPet = petShop.petDetailsCanBeUpdated(newPet).as(Pet.class);

        //Assert
        assertEquals(petPostResponseBody, updatedPet);

    }

    @After
    public void cleanup() {
        petShop.petCanBeRemovedFromStore(petId)
            .then()
            .statusCode(HttpStatus.SC_OK);
    }
}