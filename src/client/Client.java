package client;

public class Client{

    Client(int id, String name){
        this.id = id;
        this.name = name;
    }
    private int id;
    private String name;

    //Getery i settery

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
