package main_folder.Model;

public class Claim {
    private String id;
    private String Claim_Date;
    private String Exam_Date;
    private String Claim_amount;
    private String Insured_Person;
    private String Status;
    private String Documents;
    private String Receiver_Banking_Infor;

    public Claim(String id, String claim_Date, String exam_Date, String claim_amount, String insured_Person, String status, String documents, String receiver_Banking_Infor) {
        this.id = id;
        Claim_Date = claim_Date;
        Exam_Date = exam_Date;
        Claim_amount = claim_amount;
        Insured_Person = insured_Person;
        Status = status;
        Documents = documents;
        Receiver_Banking_Infor = receiver_Banking_Infor;
    }

    public String getId() {
        return id;
    }

    public String getClaim_Date() {
        return Claim_Date;
    }

    public String getExam_Date() {
        return Exam_Date;
    }

    public String getClaim_amount() {
        return Claim_amount;
    }

    public String getInsured_Person() {
        return Insured_Person;
    }

    public String getStatus() {
        return Status;
    }

    public String getDocuments() {
        return Documents;
    }

    public String getReceiver_Banking_Infor() {
        return Receiver_Banking_Infor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClaim_Date(String claim_Date) {
        Claim_Date = claim_Date;
    }

    public void setExam_Date(String exam_Date) {
        Exam_Date = exam_Date;
    }

    public void setClaim_amount(String claim_amount) {
        Claim_amount = claim_amount;
    }

    public void setInsured_Person(String insured_Person) {
        Insured_Person = insured_Person;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setDocuments(String documents) {
        Documents = documents;
    }

    public void setReceiver_Banking_Infor(String receiver_Banking_Infor) {
        Receiver_Banking_Infor = receiver_Banking_Infor;
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id='" + id + '\'' +
                ", Claim_Date='" + Claim_Date + '\'' +
                ", Exam_Date='" + Exam_Date + '\'' +
                ", Claim_amount='" + Claim_amount + '\'' +
                ", Insured_Person='" + Insured_Person + '\'' +
                ", Status='" + Status + '\'' +
                ", Documents='" + Documents + '\'' +
                ", Receiver_Banking_Infor='" + Receiver_Banking_Infor + '\'' +
                '}';
    }
}
