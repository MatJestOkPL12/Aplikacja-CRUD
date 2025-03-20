package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDao implements Dao<Client>{

    public List <Client> clients = new ArrayList<>();
    @Override
    public Optional<Client> get(String name) {
        return clients.stream().filter(client -> client.getName().equals(name)).findFirst();
    }

    @Override
    public List<Client> getAll() {
        return new ArrayList<>(clients);
    }

    @Override
    public void save(Client client) {
        clients.add(client);
    }


    @Override
    public void delete(Client client) {
        clients.remove(client);
    }
}
