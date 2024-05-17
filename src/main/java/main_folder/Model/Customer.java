package main_folder.Model;

public class Customer {
    private int id;
    private int insuranceNumber;
    public Customer() {
    }

    public Customer(int id, int insuranceNumber) {
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
