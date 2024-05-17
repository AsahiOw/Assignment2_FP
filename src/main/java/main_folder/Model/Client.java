package main_folder.Model;

public class Client {
    private int id;
    private int insuranceNumber;
    public Client() {
    }

    public Client(int id, int insuranceNumber) {
        this.id = id;
        this.insuranceNumber = insuranceNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(int insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }
}
