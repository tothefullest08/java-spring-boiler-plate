package harry.boilerplate.order.command.domain.aggregate;

import harry.boilerplate.order.command.domain.valueObject.OrderId;
import harry.boilerplate.order.command.domain.valueObject.UserId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    void save(Order order);
    Order findById(OrderId orderId);
    Optional<Order> findByIdOptional(OrderId orderId);
    List<Order> findByUserId(UserId userId);
    boolean existsById(OrderId orderId);
    void delete(Order order);
    void deleteById(OrderId orderId);
}




