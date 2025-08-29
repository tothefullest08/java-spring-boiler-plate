package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.domain.aggregate.Shop;
import harry.boilerplate.shop.domain.aggregate.ShopRepository;
import harry.boilerplate.shop.domain.valueObject.ShopId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shop Repository JPA 구현체
 * Command 측면의 쓰기 작업을 담당
 */
@Repository
@Transactional
public class ShopRepositoryImpl implements ShopRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void save(Shop shop) {
        if (shop == null) {
            throw new IllegalArgumentException("Shop cannot be null");
        }
        
        if (existsById(shop.getId())) {
            entityManager.merge(shop);
        } else {
            entityManager.persist(shop);
        }
        
        // 도메인 이벤트 처리는 별도 이벤트 퍼블리셔에서 담당
        entityManager.flush();
    }
    
    @Override
    public Shop findById(ShopId shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("ShopId cannot be null");
        }
        
        try {
            return entityManager.createQuery(
                "SELECT s FROM Shop s WHERE s.id = :id", Shop.class)
                .setParameter("id", shopId.getValue())
                .getSingleResult();
        } catch (NoResultException e) {
            throw new ShopNotFoundException(shopId);
        }
    }
    
    @Override
    public boolean existsById(ShopId shopId) {
        if (shopId == null) {
            return false;
        }
        
        Long count = entityManager.createQuery(
            "SELECT COUNT(s) FROM Shop s WHERE s.id = :id", Long.class)
            .setParameter("id", shopId.getValue())
            .getSingleResult();
            
        return count > 0;
    }
    
    @Override
    public void delete(Shop shop) {
        if (shop == null) {
            throw new IllegalArgumentException("Shop cannot be null");
        }
        
        Shop managedShop = entityManager.find(Shop.class, shop.getId().getValue());
        if (managedShop != null) {
            entityManager.remove(managedShop);
            entityManager.flush();
        }
    }
    
    @Override
    public void deleteById(ShopId shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("ShopId cannot be null");
        }
        
        Shop shop = entityManager.find(Shop.class, shopId.getValue());
        if (shop != null) {
            entityManager.remove(shop);
            entityManager.flush();
        }
    }
}