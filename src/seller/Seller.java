package seller;

import java.util.ArrayList;
import java.util.List;

public class Seller {

    public Seller(int id, String name){
        this.id = id;
        this.name = name;
    }
    public List<String> eventName = new ArrayList<>();
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
