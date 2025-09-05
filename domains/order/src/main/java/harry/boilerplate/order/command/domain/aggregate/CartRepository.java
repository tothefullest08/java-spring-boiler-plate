package harry.boilerplate.order.command.domain.aggregate;

import harry.boilerplate.order.command.domain.valueObject.CartId;
import harry.boilerplate.order.command.domain.valueObject.UserId;

import java.util.Optional;

public interface CartRepository {
    void save(Cart cart);
    Cart findById(CartId cartId);
    Cart findByUserId(UserId userId);
    Optional<Cart> findByIdOptional(CartId cartId);
    Optional<Cart> findByUserIdOptional(UserId userId);
    boolean existsById(CartId cartId);
    boolean existsByUserId(UserId userId);
    void delete(Cart cart);
    void deleteById(CartId cartId);
    void deleteByUserId(UserId userId);
}




