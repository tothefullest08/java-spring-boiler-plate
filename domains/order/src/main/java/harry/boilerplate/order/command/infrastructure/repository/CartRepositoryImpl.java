package harry.boilerplate.order.command.infrastructure.repository;

import harry.boilerplate.order.command.domain.aggregate.Cart;
import harry.boilerplate.order.command.domain.aggregate.CartRepository;
import harry.boilerplate.order.command.domain.valueObject.CartId;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class CartRepositoryImpl implements CartRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart cannot be null");
        if (existsById(cart.getId())) {
            entityManager.merge(cart);
        } else {
            entityManager.persist(cart);
        }
        entityManager.flush();
    }

    @Override
    public Cart findById(CartId cartId) {
        if (cartId == null) throw new IllegalArgumentException("CartId cannot be null");
        try {
            return entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.id = :id", Cart.class)
                .setParameter("id", cartId.getValue())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Cart findByUserId(UserId userId) {
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        try {
            return entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.userId = :userId ORDER BY c.updatedAt DESC", Cart.class)
                .setParameter("userId", userId.getValue())
                .setMaxResults(1)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean existsById(CartId cartId) {
        if (cartId == null) return false;
        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM Cart c WHERE c.id = :id", Long.class)
            .setParameter("id", cartId.getValue())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public void delete(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart cannot be null");
        Cart managed = entityManager.find(Cart.class, cart.getId().getValue());
        if (managed != null) {
            entityManager.remove(managed);
            entityManager.flush();
        }
    }

    @Override
    public void deleteById(CartId cartId) {
        if (cartId == null) throw new IllegalArgumentException("CartId cannot be null");
        Cart managed = entityManager.find(Cart.class, cartId.getValue());
        if (managed != null) {
            entityManager.remove(managed);
            entityManager.flush();
        }
    }

    @Override
    public Optional<Cart> findByIdOptional(CartId cartId) {
        return Optional.ofNullable(findById(cartId));
    }

    @Override
    public Optional<Cart> findByUserIdOptional(UserId userId) {
        return Optional.ofNullable(findByUserId(userId));
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        if (userId == null) return false;
        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId", Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public void deleteByUserId(UserId userId) {
        if (userId == null) throw new IllegalArgumentException("UserId cannot be null");
        entityManager.createQuery("DELETE FROM Cart c WHERE c.userId = :userId")
            .setParameter("userId", userId.getValue())
            .executeUpdate();
        entityManager.flush();
    }
}




