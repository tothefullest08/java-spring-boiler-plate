package harry.boilerplate.shop.query.infrastructure.dao;

import harry.boilerplate.shop.query.application.readModel.*;
import harry.boilerplate.shop.query.infrastructure.mapper.MenuReadModelMapper;
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
 * Menu Query DAO JPA 구현체
 * Query 측면의 읽기 작업을 담당 (EntityManager 직접 사용)
 */
@Repository
@Transactional(readOnly = true)
public class MenuQueryDaoImpl implements MenuQueryDao {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final MenuReadModelMapper mapper;
    
    public MenuQueryDaoImpl(MenuReadModelMapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
    public MenuBoardViewModel getMenuBoard(String shopId) {
        // 가게 정보 조회
        String shopJpql = """
            SELECT s.id, s.name, s.businessHours.openTime, s.businessHours.closeTime
            FROM Shop s
            WHERE s.id = :shopId
            """;
            
        List<Object[]> shopResults = entityManager.createQuery(shopJpql, Object[].class)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        if (shopResults.isEmpty()) {
            throw new IllegalArgumentException("Shop not found: " + shopId);
        }
        
        Object[] shopRow = shopResults.get(0);
        String shopName = (String) shopRow[1];
        LocalTime openTime = (LocalTime) shopRow[2];
        LocalTime closeTime = (LocalTime) shopRow[3];
        boolean shopIsOpen = isCurrentlyOpen(openTime, closeTime);
        
        // 메뉴 정보 조회
        List<MenuSummaryReadModel> allMenus = findMenuSummariesByShopId(shopId);
        List<MenuSummaryReadModel> openMenus = allMenus.stream()
            .filter(MenuSummaryReadModel::isOpen)
            .toList();
        List<MenuSummaryReadModel> closedMenus = allMenus.stream()
            .filter(menu -> !menu.isOpen())
            .toList();
            
        return new MenuBoardViewModel(shopId, shopName, shopIsOpen, openMenus, closedMenus);
    }
    
    @Override
    public List<MenuSummaryReadModel> findMenuSummariesByShopId(String shopId) {
        String jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId
            ORDER BY m.createdAt
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new MenuSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // shopId
                (String) row[2],           // name
                (String) row[3],           // description
                (BigDecimal) row[4],       // basePrice
                (Boolean) row[5],          // isOpen
                ((Long) row[6]).intValue() // optionGroupCount
            ))
            .toList();
    }
    
    @Override
    public List<MenuSummaryReadModel> findOpenMenuSummariesByShopId(String shopId) {
        String jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId AND m.open = true
            ORDER BY m.createdAt
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("shopId", shopId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .setHint(AvailableHints.HINT_CACHEABLE, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new MenuSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // shopId
                (String) row[2],           // name
                (String) row[3],           // description
                (BigDecimal) row[4],       // basePrice
                (Boolean) row[5],          // isOpen
                ((Long) row[6]).intValue() // optionGroupCount
            ))
            .toList();
    }
    
    @Override
    public Optional<MenuDetailReadModel> findMenuDetail(String menuId) {
        String jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   m.createdAt, m.updatedAt
            FROM Menu m
            WHERE m.id = :menuId
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        if (results.isEmpty()) {
            return Optional.empty();
        }
        
        Object[] row = results.get(0);
        
        // 옵션그룹 정보 조회
        List<OptionGroupReadModel> optionGroups = findOptionGroupsByMenuId(menuId);
        
        return Optional.of(new MenuDetailReadModel(
            (String) row[0],           // id
            (String) row[1],           // shopId
            (String) row[2],           // name
            (String) row[3],           // description
            (BigDecimal) row[4],       // basePrice
            (Boolean) row[5],          // isOpen
            optionGroups,              // optionGroups
            (Instant) row[6],          // createdAt
            (Instant) row[7]           // updatedAt
        ));
    }
    
    @Override
    public boolean existsMenu(String menuId) {
        String jpql = "SELECT COUNT(m) FROM Menu m WHERE m.id = :menuId";
        
        Long count = entityManager.createQuery(jpql, Long.class)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getSingleResult();
            
        return count > 0;
    }
    
    @Override
    public List<MenuSummaryReadModel> searchMenusByName(String shopId, String nameKeyword) {
        String jpql = """
            SELECT m.id, m.shopId, m.name, m.description, m.basePrice, m.open,
                   (SELECT COUNT(og) FROM OptionGroupEntity og WHERE og.menu.id = m.id)
            FROM Menu m
            WHERE m.shopId = :shopId AND m.name LIKE :nameKeyword
            ORDER BY m.name
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("shopId", shopId)
            .setParameter("nameKeyword", "%" + nameKeyword + "%")
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new MenuSummaryReadModel(
                (String) row[0],           // id
                (String) row[1],           // shopId
                (String) row[2],           // name
                (String) row[3],           // description
                (BigDecimal) row[4],       // basePrice
                (Boolean) row[5],          // isOpen
                ((Long) row[6]).intValue() // optionGroupCount
            ))
            .toList();
    }
    
    /**
     * 메뉴의 옵션그룹 정보 조회
     */
    private List<OptionGroupReadModel> findOptionGroupsByMenuId(String menuId) {
        String jpql = """
            SELECT og.id, og.name, og.required
            FROM OptionGroupEntity og
            WHERE og.menu.id = :menuId
            ORDER BY og.createdAt
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("menuId", menuId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        return results.stream()
            .map(row -> {
                String optionGroupId = (String) row[0];
                String name = (String) row[1];
                Boolean required = (Boolean) row[2];
                
                List<OptionReadModel> options = findOptionsByOptionGroupId(optionGroupId);
                
                return new OptionGroupReadModel(optionGroupId, name, required, options);
            })
            .toList();
    }
    
    /**
     * 옵션그룹의 옵션 정보 조회
     */
    private List<OptionReadModel> findOptionsByOptionGroupId(String optionGroupId) {
        String jpql = """
            SELECT o.name, o.price
            FROM OptionEntity o
            WHERE o.optionGroup.id = :optionGroupId
            ORDER BY o.createdAt
            """;
            
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class)
            .setParameter("optionGroupId", optionGroupId)
            .setHint(AvailableHints.HINT_READ_ONLY, true)
            .getResultList();
            
        return results.stream()
            .map(row -> new OptionReadModel(
                (String) row[0],      // name
                (BigDecimal) row[1]   // price
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