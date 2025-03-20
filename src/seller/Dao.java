package seller;

import java.util.List;
import java.util.Optional;

public interface Dao <Seller> {
    Optional<Seller> get(String name);
    List<Seller> getAll();
    void save(Seller seller);
    void delete(Seller seller);

}
