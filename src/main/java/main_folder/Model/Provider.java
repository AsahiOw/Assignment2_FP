/**
 * @Nguyen Ha Tuan Nguyen - s3978072
 * @Luong Tuan Kiet - s3980288
 * @Tran Tuan Minh - s3804812
 * @Nguyen Thanh Tung - s3979489
 * <Ooptional>
 */
package main_folder.Model;

import javafx.beans.property.SimpleStringProperty;

public class Provider {
    private SimpleStringProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty email;
    private SimpleStringProperty password;
    private SimpleStringProperty userType;
    private SimpleStringProperty idDependency;

    public Provider(String id, String name, String email) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }
    public Provider(String id, String name, String email, String password, String userType, String idDependency) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.userType = new SimpleStringProperty(userType);
        this.idDependency = new SimpleStringProperty(idDependency);
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

    public String getIdDependency() {
        return idDependency.get();
    }
}