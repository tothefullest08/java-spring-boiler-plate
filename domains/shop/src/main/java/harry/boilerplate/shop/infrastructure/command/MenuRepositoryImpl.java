package harry.boilerplate.shop.infrastructure.command;

import harry.boilerplate.shop.domain.Menu;
import harry.boilerplate.shop.domain.MenuId;
import harry.boilerplate.shop.domain.MenuRepository;
import harry.boilerplate.shop.domain.ShopId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Menu Repository JPA 구현체
 * Command 측면의 쓰기 작업을 담당
 */
@Repository
@Transactional
public class MenuRepositoryImpl implements MenuRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void save(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }
        
        if (existsById(menu.getId())) {
            entityManager.merge(menu);
        } else {
            entityManager.persist(menu);
        }
        
        // 도메인 이벤트 처리는 별도 이벤트 퍼블리셔에서 담당
        entityManager.flush();
    }
    
    @Override
    public Menu findById(MenuId menuId) {
        if (menuId == null) {
            throw new IllegalArgumentException("MenuId cannot be null");
        }
        
        try {
            return entityManager.createQuery(
                "SELECT m FROM Menu m WHERE m.id = :id", Menu.class)
                .setParameter("id", menuId.getValue())
                .getSingleResult();
        } catch (NoResultException e) {
            throw new MenuNotFoundException(menuId);
        }
    }
    
    @Override
    public List<Menu> findByShopId(ShopId shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("ShopId cannot be null");
        }
        
        return entityManager.createQuery(
            "SELECT m FROM Menu m WHERE m.shopId = :shopId ORDER BY m.createdAt", Menu.class)
            .setParameter("shopId", shopId.getValue())
            .getResultList();
    }
    
    @Override
    public boolean existsById(MenuId menuId) {
        if (menuId == null) {
            return false;
        }
        
        Long count = entityManager.createQuery(
            "SELECT COUNT(m) FROM Menu m WHERE m.id = :id", Long.class)
            .setParameter("id", menuId.getValue())
            .getSingleResult();
            
        return count > 0;
    }
    
    @Override
    public void delete(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }
        
        Menu managedMenu = entityManager.find(Menu.class, menu.getId().getValue());
        if (managedMenu != null) {
            entityManager.remove(managedMenu);
            entityManager.flush();
        }
    }
    
    @Override
    public void deleteById(MenuId menuId) {
        if (menuId == null) {
            throw new IllegalArgumentException("MenuId cannot be null");
        }
        
        Menu menu = entityManager.find(Menu.class, menuId.getValue());
        if (menu != null) {
            entityManager.remove(menu);
            entityManager.flush();
        }
    }
    
    @Override
    public void deleteByShopId(ShopId shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("ShopId cannot be null");
        }
        
        entityManager.createQuery(
            "DELETE FROM Menu m WHERE m.shopId = :shopId")
            .setParameter("shopId", shopId.getValue())
            .executeUpdate();
            
        entityManager.flush();
    }
}