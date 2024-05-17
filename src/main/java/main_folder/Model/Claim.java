package main_folder.Model;

import java.sql.Date;

public class Claim {
    private Integer id;
    private Date Claim_Date;
    private Date Exam_Date;
    private Double Claim_amount;
    private String Insured_Person;
    private String Status;
    private String Documents;
    private String Receiver_Banking_Infor;
    public Claim() {}

    public Claim(Integer id, Date claim_Date, Date exam_Date, Double claim_amount, String insured_Person, String status, String documents, String receiver_Banking_Infor) {
        this.id = id;
        Claim_Date = claim_Date;
        Exam_Date = exam_Date;
        Claim_amount = claim_amount;
        Insured_Person = insured_Person;
        Status = status;
        Documents = documents;
        Receiver_Banking_Infor = receiver_Banking_Infor;
    }

    public Integer getId() {
        return id;
    }

    public Date getClaim_Date() {
        return Claim_Date;
    }

    public Date getExam_Date() {
        return Exam_Date;
    }

    public Double getClaim_amount() {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setClaim_Date(Date claim_Date) {
        Claim_Date = claim_Date;
    }

    public void setExam_Date(Date exam_Date) {
        Exam_Date = exam_Date;
    }

    public void setClaim_amount(Double claim_amount) {
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
