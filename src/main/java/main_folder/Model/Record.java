package main_folder.Model;

public class Record {
    private String id;
    private String context;
    private String date;

    public Record(String id, String context, String date) {
        this.id = id;
        this.context = context;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}