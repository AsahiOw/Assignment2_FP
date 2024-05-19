/**
 * @Nguyen Ha Tuan Nguyen - s3978072
 * @Luong Tuan Kiet - s3980288
 * @Tran Tuan Minh - s3804812
 * @Nguyen Thanh Tung - s3979489
 * <Ooptional>
 */
package main_folder.Model;

public class Surveyor {
    private int id;
    private int userId;
    private String name;
    private String email;
    private String password;
    private String userType;

    public Surveyor() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Surveyor(int id, int userId, String name, String password, String userType) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Surveyor{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}

