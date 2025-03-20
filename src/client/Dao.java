package client;

import java.util.List;
import java.util.Optional;

public interface Dao <Client> {

    Optional<Client> get(String name);
    List<Client> getAll();
    void save(Client client);
    void delete(Client client);


}
