package seller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellerDao implements Dao<Seller> {
    List<Seller> sellers = new ArrayList<>();
    @Override
    public Optional<Seller> get(String name) {
        return Optional.empty();
    }

    @Override
    public List<Seller> getAll() {
        return new ArrayList<>(sellers);
    }

    @Override
    public void save(Seller seller) {
        sellers.add(seller);
    }

    @Override
    public void delete(Seller seller) {
        sellers.remove(seller);
    }
}
