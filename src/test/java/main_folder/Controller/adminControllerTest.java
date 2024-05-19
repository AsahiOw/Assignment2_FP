package main_folder.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

class adminControllerTest {
    private adminController adminController;
    private Random random;
    private String[] userTypes = {"Customer", "Provider"};

    @BeforeEach
    void setUp() {
        adminController = new adminController();
        random = new Random();
    }

    @Test
    void testUpdateCustomerRecord() {
        int randomId = random.nextInt(1000); // Generate a random id between 0 and 999
        adminController.UpdateCustomerRecord(randomId);
        // Add assertions here
    }

    @Test
    void testDeleteCustomerRecord() {
        int randomId = random.nextInt(1000); // Generate a random id between 0 and 999
        adminController.DeleteCustomerRecord(randomId);
        // Add assertions here
    }

    @Test
    void testRetrieveCustomerRecord() {
        int randomId = random.nextInt(1000); // Generate a random id between 0 and 999
        String randomUserType = userTypes[random.nextInt(userTypes.length)]; // Select a random user type
        adminController.RetrieveCustomerRecord(randomId, randomUserType);
        // Add assertions here
    }

    @Test
    void testUpdateProviderRecord() {
        int randomId = random.nextInt(1000); // Generate a random id between 0 and 999
        adminController.UpdateProviderRecord(randomId);
        // Add assertions here
    }

    @Test
    void testDeleteProviderRecord() {
        int randomId = random.nextInt(1000); // Generate a random id between 0 and 999
        adminController.DeleteProviderRecord(randomId);
        // Add assertions here
    }

    @Test
    void testRetrieveProviderRecord() {
        adminController.RetrieveProviderRecord(1, "AdminTest");
        // Add assertions here
    }
}