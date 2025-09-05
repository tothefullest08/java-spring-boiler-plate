package harry.boilerplate.order.query.infrastructure.dao;

import harry.boilerplate.order.query.application.readmodel.CartItemReadModel;
import harry.boilerplate.order.query.application.readmodel.CartSummaryReadModel;
import harry.boilerplate.order.query.application.readmodel.SelectedOptionReadModel;
import harry.boilerplate.order.command.domain.valueObject.CartId;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 장바구니 조회 최적화를 위한 Query DAO 구현체
 * EntityManager를 직접 사용하여 읽기 최적화 쿼리 수행
 */
@Repository
@Transactional(readOnly = true)
public class CartQueryDaoImpl implements CartQueryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CartSummaryReadModel> findCartSummaryByUserId(UserId userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            // 장바구니 기본 정보 조회
            Object[] cartData = entityManager.createQuery(
                "SELECT c.id, c.userId, c.shopId, c.updatedAt " +
                "FROM Cart c WHERE c.userId = :userId", Object[].class)
                .setParameter("userId", userId.getValue())
                .getSingleResult();

            String cartId = (String) cartData[0];
            String userIdValue = (String) cartData[1];
            String shopId = (String) cartData[2];
            Instant updatedAt = (Instant) cartData[3];

            // 장바구니 아이템들 조회
            List<Object[]> itemsData = entityManager.createQuery(
                "SELECT cli.id, cli.menuId, cli.quantity " +
                "FROM CartLineItem cli WHERE cli.cart.id = :cartId", Object[].class)
                .setParameter("cartId", cartId)
                .getResultList();

            List<CartItemReadModel> items = new ArrayList<>();
            BigDecimal totalPrice = BigDecimal.ZERO;
            int totalQuantity = 0;

            for (Object[] itemData : itemsData) {
                String itemId = (String) itemData[0];
                String menuId = (String) itemData[1];
                int quantity = (Integer) itemData[2];

                // TODO: Shop Context API를 통해 메뉴 정보와 가격 조회
                String menuName = "메뉴명"; // Shop API 호출 필요
                BigDecimal menuPrice = BigDecimal.valueOf(10000); // Shop API 호출 필요
                
                // 선택된 옵션들 조회
                List<String> optionIds = entityManager.createQuery(
                    "SELECT cio.optionId FROM CartLineItem cli " +
                    "JOIN cli.selectedOptionIds cio WHERE cli.id = :itemId", String.class)
                    .setParameter("itemId", itemId)
                    .getResultList();

                List<SelectedOptionReadModel> selectedOptions = new ArrayList<>();
                for (String optionId : optionIds) {
                    // TODO: Shop Context API를 통해 옵션 정보 조회
                    selectedOptions.add(new SelectedOptionReadModel(
                        optionId, "옵션명", BigDecimal.ZERO)); // Shop API 호출 필요
                }

                BigDecimal linePrice = menuPrice.multiply(BigDecimal.valueOf(quantity));
                
                items.add(new CartItemReadModel(
                    itemId, menuId, menuName, menuPrice, selectedOptions, quantity, linePrice));
                
                totalPrice = totalPrice.add(linePrice);
                totalQuantity += quantity;
            }

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = shopId != null ? "가게명" : null; // Shop API 호출 필요

            return Optional.of(new CartSummaryReadModel(
                cartId, userIdValue, shopId, shopName, items, totalPrice, totalQuantity, updatedAt));

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CartSummaryReadModel> findCartSummaryById(CartId cartId) {
        if (cartId == null) {
            return Optional.empty();
        }

        try {
            // 장바구니 기본 정보 조회
            Object[] cartData = entityManager.createQuery(
                "SELECT c.id, c.userId, c.shopId, c.updatedAt " +
                "FROM Cart c WHERE c.id = :cartId", Object[].class)
                .setParameter("cartId", cartId.getValue())
                .getSingleResult();

            String userIdValue = (String) cartData[1];
            return findCartSummaryByUserId(UserId.of(userIdValue));

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsCartByUserId(UserId userId) {
        if (userId == null) {
            return false;
        }

        Long count = entityManager.createQuery(
            "SELECT COUNT(c) FROM Cart c WHERE c.userId = :userId", Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();

        return count > 0;
    }

    @Override
    public int getCartItemCountByUserId(UserId userId) {
        if (userId == null) {
            return 0;
        }

        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(cli) FROM CartLineItem cli " +
                "WHERE cli.cart.userId = :userId", Long.class)
                .setParameter("userId", userId.getValue())
                .getSingleResult();

            return count.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
}