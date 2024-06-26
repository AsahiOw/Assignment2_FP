/**
 * @Nguyen Ha Tuan Nguyen - s3978072
 * @Luong Tuan Kiet - s3980288
 * @Tran Tuan Minh - s3804812
 * @Nguyen Thanh Tung - s3979489
 * <Ooptional>
 */
package main_folder.Model;

import javafx.beans.property.SimpleStringProperty;

public class Customer {
    private SimpleStringProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty email;
    private SimpleStringProperty password;
    private SimpleStringProperty userType;
    private SimpleStringProperty insuranceNumber;
    private SimpleStringProperty idDependency;

    public Customer(String id, String name, String email, String password, String userType, String insuranceNumber, String idDependency) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.userType = new SimpleStringProperty(userType);
        this.insuranceNumber = new SimpleStringProperty(insuranceNumber);
        this.idDependency = new SimpleStringProperty(idDependency);
    }

    public Customer(String id, String name, String email, String userType, String insuranceNumber) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.userType = new SimpleStringProperty(userType);
        this.insuranceNumber = new SimpleStringProperty(insuranceNumber);
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getPassword() {
        return password.get();
    }

    public String getUserType() {
        return userType.get();
    }

    public String getInsuranceNumber() {
        return insuranceNumber.get();
    }

    public String getIdDependency() {
        return idDependency.get();
    }
}
