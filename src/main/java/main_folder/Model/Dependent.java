package main_folder.Model;


public class Dependent {
    private String id;
    private String name;
    private String email;
    private String policyNumber;

    public Dependent(String id, String name, String email, String policyNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.policyNumber = policyNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    @Override
    public String toString() {
        return "Dependent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                '}';
    }
}