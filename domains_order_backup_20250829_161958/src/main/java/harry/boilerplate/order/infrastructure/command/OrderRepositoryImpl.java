package harry.boilerplate.order.infrastructure.command;

import harry.boilerplate.order.domain.aggregate.Order;
import harry.boilerplate.order.domain.aggregate.OrderRepository;
import harry.boilerplate.order.domain.valueObject.OrderId;
import harry.boilerplate.order.domain.valueObject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class OrderRepositoryImpl implements OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        if (existsById(order.getId())) {
            entityManager.merge(order);
        } else {
            entityManager.persist(order);
        }
        entityManager.flush();
    }

    @Override
    public Order findById(OrderId orderId) {
        if (orderId == null) throw new IllegalArgumentException("OrderId cannot be null");
        try {
            return entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.id = :id", Order.class)
                .setParameter("id", orderId.getValue())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsById(OrderId orderId) {
        if (orderId == null) return false;
        Long count = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.id = :id", Long.class)
            .setParameter("id", orderId.getValue())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public void delete(Order order) {
        if (order == null) throw new IllegalArgumentException("Order cannot be null");
        Order managed = entityManager.find(Order.class, order.getId().getValue());
        if (managed != null) {
            entityManager.remove(managed);
            entityManager.flush();
        }
    }

    @Override
    public void deleteById(OrderId orderId) {
        if (orderId == null) throw new IllegalArgumentException("OrderId cannot be null");
        Order managed = entityManager.find(Order.class, orderId.getValue());
        if (managed != null) {
            entityManager.remove(managed);
            entityManager.flush();
        }
    }

    @Override
    public Optional<Order> findByIdOptional(OrderId orderId) {
        return Optional.ofNullable(findById(orderId));
    }

    @Override
    public List<Order> findByUserId(UserId userId) {
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        return entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderTime DESC", Order.class)
            .setParameter("userId", userId.getValue())
            .getResultList();
    }
}


