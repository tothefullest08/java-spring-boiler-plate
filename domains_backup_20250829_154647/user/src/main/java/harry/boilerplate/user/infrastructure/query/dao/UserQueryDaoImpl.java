package harry.boilerplate.user.infrastructure.query.dao;

import harry.boilerplate.user.application.query.readmodel.UserDetailReadModel;
import harry.boilerplate.user.application.query.readmodel.UserSummaryReadModel;
import harry.boilerplate.user.domain.aggregate.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Query DAO JPA 구현체
 * Query 측면의 읽기 최적화된 DAO 패턴 구현
 * Requirements 7.4: UserQueryDao 구현
 */
@Repository
@Transactional(readOnly = true)
public class UserQueryDaoImpl implements UserQueryDao {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<UserDetailReadModel> findUserDetailById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            User user = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :userId", User.class)
                .setParameter("userId", userId)
                .getSingleResult();
                
            UserDetailReadModel readModel = new UserDetailReadModel(
                user.getId().getValue(),
                user.getName(),
                user.getEmail(),
                user.isValid(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
            );
            
            return Optional.of(readModel);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<UserSummaryReadModel> findUserSummaryByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            User user = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
                
            UserSummaryReadModel readModel = new UserSummaryReadModel(
                user.getId().getValue(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt().toString()
            );
            
            return Optional.of(readModel);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<UserSummaryReadModel> findAllUserSummaries() {
        List<User> users = entityManager.createQuery(
            "SELECT u FROM User u ORDER BY u.createdAt DESC", User.class)
            .getResultList();
            
        return users.stream()
            .map(user -> new UserSummaryReadModel(
                user.getId().getValue(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt().toString()
            ))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserSummaryReadModel> findUserSummariesByNamePattern(String namePattern) {
        if (namePattern == null || namePattern.trim().isEmpty()) {
            return List.of();
        }
        
        String likePattern = "%" + namePattern.trim() + "%";
        
        List<User> users = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.name LIKE :namePattern ORDER BY u.name", User.class)
            .setParameter("namePattern", likePattern)
            .getResultList();
            
        return users.stream()
            .map(user -> new UserSummaryReadModel(
                user.getId().getValue(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt().toString()
            ))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }
        
        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.id = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public UserDetailReadModel findUserDetail(String userId) {
        Optional<UserDetailReadModel> result = findUserDetailById(userId);
        return result.orElse(null);
    }
    
    @Override
    public long countAllUsers() {
        try {
            return entityManager.createQuery(
                "SELECT COUNT(u) FROM User u", Long.class)
                .getSingleResult();
        } catch (Exception e) {
            return 0L;
        }
    }
}