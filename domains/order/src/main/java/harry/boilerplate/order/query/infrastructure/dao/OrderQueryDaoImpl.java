package harry.boilerplate.order.query.infrastructure.dao;

import harry.boilerplate.order.query.application.readModel.*;
import harry.boilerplate.order.command.domain.valueObject.OrderId;
import harry.boilerplate.order.command.domain.valueObject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 주문 조회 최적화를 위한 Query DAO 구현체
 * EntityManager를 직접 사용하여 읽기 최적화 쿼리 수행
 */
@Repository
@Transactional(readOnly = true)
public class OrderQueryDaoImpl implements OrderQueryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<OrderHistoryReadModel> findOrderHistoryByUserId(UserId userId, int page, int size) {
        if (userId == null) {
            return List.of();
        }

        List<Object[]> ordersData = entityManager.createQuery(
            "SELECT o.id, o.userId, o.shopId, o.totalPrice, o.orderTime " +
            "FROM Order o WHERE o.userId = :userId " +
            "ORDER BY o.orderTime DESC", Object[].class)
            .setParameter("userId", userId.getValue())
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();

        List<OrderHistoryReadModel> result = new ArrayList<>();
        
        for (Object[] orderData : ordersData) {
            String orderId = (String) orderData[0];
            String userIdValue = (String) orderData[1];
            String shopId = (String) orderData[2];
            BigDecimal totalPrice = (BigDecimal) orderData[3];
            LocalDateTime orderTime = (LocalDateTime) orderData[4];

            // 주문 아이템들 조회
            List<OrderItemReadModel> orderItems = getOrderItemsByOrderId(orderId);
            int totalQuantity = orderItems.stream()
                .mapToInt(OrderItemReadModel::getQuantity)
                .sum();

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = "가게명"; // Shop API 호출 필요

            result.add(new OrderHistoryReadModel(
                orderId, userIdValue, shopId, shopName, orderItems, 
                totalPrice, totalQuantity, orderTime));
        }

        return result;
    }

    @Override
    public Optional<OrderHistoryReadModel> findOrderHistoryById(OrderId orderId) {
        if (orderId == null) {
            return Optional.empty();
        }

        try {
            Object[] orderData = entityManager.createQuery(
                "SELECT o.id, o.userId, o.shopId, o.totalPrice, o.orderTime " +
                "FROM Order o WHERE o.id = :orderId", Object[].class)
                .setParameter("orderId", orderId.getValue())
                .getSingleResult();

            String orderIdValue = (String) orderData[0];
            String userId = (String) orderData[1];
            String shopId = (String) orderData[2];
            BigDecimal totalPrice = (BigDecimal) orderData[3];
            LocalDateTime orderTime = (LocalDateTime) orderData[4];

            // 주문 아이템들 조회
            List<OrderItemReadModel> orderItems = getOrderItemsByOrderId(orderIdValue);
            int totalQuantity = orderItems.stream()
                .mapToInt(OrderItemReadModel::getQuantity)
                .sum();

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = "가게명"; // Shop API 호출 필요

            return Optional.of(new OrderHistoryReadModel(
                orderIdValue, userId, shopId, shopName, orderItems, 
                totalPrice, totalQuantity, orderTime));

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderSummaryReadModel> findRecentOrdersByUserId(UserId userId, int limit) {
        if (userId == null) {
            return List.of();
        }

        List<Object[]> ordersData = entityManager.createQuery(
            "SELECT o.id, o.shopId, o.totalPrice, o.orderTime " +
            "FROM Order o WHERE o.userId = :userId " +
            "ORDER BY o.orderTime DESC", Object[].class)
            .setParameter("userId", userId.getValue())
            .setMaxResults(limit)
            .getResultList();

        List<OrderSummaryReadModel> result = new ArrayList<>();

        for (Object[] orderData : ordersData) {
            String orderId = (String) orderData[0];
            String shopId = (String) orderData[1];
            BigDecimal totalPrice = (BigDecimal) orderData[2];
            LocalDateTime orderTime = (LocalDateTime) orderData[3];

            // 첫 번째 메뉴와 추가 메뉴 개수 조회
            List<Object[]> menuData = entityManager.createQuery(
                "SELECT oli.menuName, oli.quantity " +
                "FROM OrderLineItem oli WHERE oli.order.id = :orderId " +
                "ORDER BY oli.id", Object[].class)
                .setParameter("orderId", orderId)
                .getResultList();

            String firstMenuName = menuData.isEmpty() ? "" : (String) menuData.get(0)[0];
            int additionalMenuCount = Math.max(0, menuData.size() - 1);
            int totalQuantity = menuData.stream()
                .mapToInt(data -> (Integer) data[1])
                .sum();

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = "가게명"; // Shop API 호출 필요

            result.add(new OrderSummaryReadModel(
                orderId, shopId, shopName, totalPrice, totalQuantity, 
                orderTime, firstMenuName, additionalMenuCount));
        }

        return result;
    }

    @Override
    public List<OrderHistoryReadModel> findOrderHistoryByUserIdAndDateRange(
            UserId userId, LocalDateTime startDate, LocalDateTime endDate) {
        if (userId == null) {
            return List.of();
        }

        List<Object[]> ordersData = entityManager.createQuery(
            "SELECT o.id, o.userId, o.shopId, o.totalPrice, o.orderTime " +
            "FROM Order o WHERE o.userId = :userId " +
            "AND o.orderTime >= :startDate AND o.orderTime <= :endDate " +
            "ORDER BY o.orderTime DESC", Object[].class)
            .setParameter("userId", userId.getValue())
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getResultList();

        List<OrderHistoryReadModel> result = new ArrayList<>();

        for (Object[] orderData : ordersData) {
            String orderId = (String) orderData[0];
            String userIdValue = (String) orderData[1];
            String shopId = (String) orderData[2];
            BigDecimal totalPrice = (BigDecimal) orderData[3];
            LocalDateTime orderTime = (LocalDateTime) orderData[4];

            List<OrderItemReadModel> orderItems = getOrderItemsByOrderId(orderId);
            int totalQuantity = orderItems.stream()
                .mapToInt(OrderItemReadModel::getQuantity)
                .sum();

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = "가게명"; // Shop API 호출 필요

            result.add(new OrderHistoryReadModel(
                orderId, userIdValue, shopId, shopName, orderItems, 
                totalPrice, totalQuantity, orderTime));
        }

        return result;
    }

    @Override
    public long countOrdersByUserId(UserId userId) {
        if (userId == null) {
            return 0;
        }

        return entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.userId = :userId", Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();
    }

    @Override
    public List<OrderHistoryReadModel> findOrderHistoryByUserIdAndShopId(UserId userId, String shopId) {
        if (userId == null || shopId == null) {
            return List.of();
        }

        List<Object[]> ordersData = entityManager.createQuery(
            "SELECT o.id, o.userId, o.shopId, o.totalPrice, o.orderTime " +
            "FROM Order o WHERE o.userId = :userId AND o.shopId = :shopId " +
            "ORDER BY o.orderTime DESC", Object[].class)
            .setParameter("userId", userId.getValue())
            .setParameter("shopId", shopId)
            .getResultList();

        List<OrderHistoryReadModel> result = new ArrayList<>();

        for (Object[] orderData : ordersData) {
            String orderId = (String) orderData[0];
            String userIdValue = (String) orderData[1];
            String shopIdValue = (String) orderData[2];
            BigDecimal totalPrice = (BigDecimal) orderData[3];
            LocalDateTime orderTime = (LocalDateTime) orderData[4];

            List<OrderItemReadModel> orderItems = getOrderItemsByOrderId(orderId);
            int totalQuantity = orderItems.stream()
                .mapToInt(OrderItemReadModel::getQuantity)
                .sum();

            // TODO: Shop Context API를 통해 가게 이름 조회
            String shopName = "가게명"; // Shop API 호출 필요

            result.add(new OrderHistoryReadModel(
                orderId, userIdValue, shopIdValue, shopName, orderItems, 
                totalPrice, totalQuantity, orderTime));
        }

        return result;
    }

    /**
     * 주문 ID로 주문 아이템들 조회 (내부 헬퍼 메서드)
     */
    private List<OrderItemReadModel> getOrderItemsByOrderId(String orderId) {
        List<Object[]> itemsData = entityManager.createQuery(
            "SELECT oli.id, oli.menuId, oli.menuName, oli.quantity, oli.linePrice " +
            "FROM OrderLineItem oli WHERE oli.order.id = :orderId", Object[].class)
            .setParameter("orderId", orderId)
            .getResultList();

        List<OrderItemReadModel> orderItems = new ArrayList<>();

        for (Object[] itemData : itemsData) {
            String itemId = (String) itemData[0];
            String menuId = (String) itemData[1];
            String menuName = (String) itemData[2];
            int quantity = (Integer) itemData[3];
            BigDecimal linePrice = (BigDecimal) itemData[4];

            // 선택된 옵션들 조회 (SelectedOption 임베디드 객체에서)
            List<Object[]> optionsData = entityManager.createQuery(
                "SELECT so.optionId, so.optionName, so.optionPrice " +
                "FROM OrderLineItem oli JOIN oli.selectedOptions so " +
                "WHERE oli.id = :itemId", Object[].class)
                .setParameter("itemId", itemId)
                .getResultList();

            List<SelectedOptionReadModel> selectedOptions = new ArrayList<>();
            for (Object[] optionData : optionsData) {
                String optionId = (String) optionData[0];
                String optionName = (String) optionData[1];
                BigDecimal optionPrice = (BigDecimal) optionData[2];
                
                selectedOptions.add(new SelectedOptionReadModel(
                    optionId, optionName, optionPrice));
            }

            orderItems.add(new OrderItemReadModel(
                itemId, menuId, menuName, selectedOptions, quantity, linePrice));
        }

        return orderItems;
    }
}