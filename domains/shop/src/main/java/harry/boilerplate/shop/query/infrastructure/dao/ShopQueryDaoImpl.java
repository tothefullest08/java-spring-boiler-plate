package harry.boilerplate.shop.query.infrastructure.dao;

import harry.boilerplate.shop.query.application.readModel.ShopDetailReadModel;
import harry.boilerplate.shop.query.application.readModel.ShopSummaryReadModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.jpa.AvailableHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Shop Query DAO JPA 구현체
 * Query 측면의 읽기 작업을 담당 (EntityManager 직접 사용)
 */
@Repository
@Transactional(readOnly = true)
public class ShopQueryDaoImpl implements ShopQueryDao {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<ShopSummaryReadModel> findAllShopSummaries() {
        String jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            ORDER BY s.createdAt DESC
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new ShopSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // name
                (BigDecimal) row[2],       // minOrderAmount
                (LocalTime) row[3],        // openTime
                (LocalTime) row[4],        // closeTime
                isCurrentlyOpen((LocalTime) row[3], (LocalTime) row[4])
            ))
            .toList();
    }
    
    @Override
    public List<ShopSummaryReadModel> findOpenShopSummaries() {
        LocalTime currentTime = LocalTime.now();
        
        String jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.businessHours.openTime IS NOT NULL 
            AND s.businessHours.closeTime IS NOT NULL
            ORDER BY s.createdAt DESC
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .getResultList();
            
        return results.stream()
            .filter(row -> isCurrentlyOpen((LocalTime) row[3], (LocalTime) row[4]))
            .map(row -> new ShopSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // name
                (BigDecimal) row[2],       // minOrderAmount
                (LocalTime) row[3],        // openTime
                (LocalTime) row[4],        // closeTime
                true                       // isOpen (이미 필터링됨)
            ))
            .toList();
    }
    
    @Override
    public Optional<ShopDetailReadModel> findShopDetail(String shopId) {
        String jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime,
                   s.createdAt, s.updatedAt
            FROM Shop s
            WHERE s.id = :shopId
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        if (results.isEmpty()) {
            return Optional.empty();
        }
        
        Object[] row = results.get(0);
        return Optional.of(new ShopDetailReadModel(
            (String) row[0],           // id
            (String) row[1],           // name
            (BigDecimal) row[2],       // minOrderAmount
            (LocalTime) row[3],        // openTime
            (LocalTime) row[4],        // closeTime
            isCurrentlyOpen((LocalTime) row[3], (LocalTime) row[4]),
            (Instant) row[5],          // createdAt
            (Instant) row[6]           // updatedAt
        ));
    }
    
    @Override
    public boolean existsShop(String shopId) {
        String jpql = "SELECT COUNT(s) FROM Shop s WHERE s.id = :shopId";
        
        Long count = entityManager.createQuery(jpql, Long.class)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getSingleResult();
            
        return count > 0;
    }
    
    @Override
    public List<ShopSummaryReadModel> searchShopsByName(String nameKeyword) {
        String jpql = """
            SELECT s.id, s.name, s.minOrderAmount, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.name LIKE :nameKeyword
            ORDER BY s.name
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("nameKeyword", "%" + nameKeyword + "%")
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new ShopSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // name
                (BigDecimal) row[2],       // minOrderAmount
                (LocalTime) row[3],        // openTime
                (LocalTime) row[4],        // closeTime
                isCurrentlyOpen((LocalTime) row[3], (LocalTime) row[4])
            ))
            .toList();
    }
    
    /**
     * 현재 시간 기준으로 영업 중인지 확인
     */
    private boolean isCurrentlyOpen(LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            return false;
        }
        
        LocalTime currentTime = LocalTime.now();
        
        // 일반적인 경우: 09:00 - 18:00
        if (openTime.isBefore(closeTime)) {
            return !currentTime.isBefore(openTime) && currentTime.isBefore(closeTime);
        }
        // 자정을 넘는 경우: 22:00 - 02:00
        else {
            return !currentTime.isBefore(openTime) || currentTime.isBefore(closeTime);
        }
    }
}