package main_folder.Model;
import java.util.List;

public class PolicyOwner {

    private String id;
    private String name;
    private String email;
    private String policyNumber;

    public PolicyOwner(String id, String name, String email, String policyNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.policyNumber = policyNumber;
    }

    private List<PolicyHolder> policyHolders;
    private double policyHolderRate;

    public void addPolicyHolder(PolicyHolder policyHolder) {
        policyHolders.add(policyHolder);
    }

    public void updatePolicyHolder(PolicyHolder oldPolicyHolder, PolicyHolder newPolicyHolder) {
        int index = policyHolders.indexOf(oldPolicyHolder);
        if (index != -1) {
            policyHolders.set(index, newPolicyHolder);
        }
    }

    public void removePolicyHolder(PolicyHolder policyHolder) {
        policyHolders.remove(policyHolder);
    }

    public double calculateYearlyInsuranceCost() {
        double totalCost = 0;
        for (PolicyHolder policyHolder : policyHolders) {
            if (policyHolder.getClass().getSimpleName().equals("Dependent")) {
                totalCost += policyHolderRate * 0.6;
            } else {
                totalCost += policyHolderRate;
            }
        }
        return totalCost;
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

    public String printForHolder() {
        return  "Your Policy Holder: " + name + "\n" +
                "Email: " + email + "\n";
    }

    @Override
    public String toString() {
        return  "Your ID: " + id + "\n" +
                "Full Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Your Insurance Number: " + policyNumber;
    }

}
