package harry.boilerplate.user.infrastructure.command;

import harry.boilerplate.user.domain.aggregate.User;
import harry.boilerplate.user.domain.aggregate.UserRepository;
import harry.boilerplate.user.domain.valueObject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User Repository JPA 구현체
 * Command 측면의 쓰기 최적화된 Repository 패턴 구현
 * Requirements 7.3: User JPA 엔티티 매핑 및 Repository 구현
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            // 기존 사용자인지 확인
            User existingUser = entityManager.find(User.class, user.getId().getValue());
            if (existingUser == null) {
                entityManager.persist(user);
            } else {
                entityManager.merge(user);
            }
        }
    }
    
    @Override
    public Optional<User> findById(UserId userId) {
        if (userId == null || userId.getValue() == null) {
            return Optional.empty();
        }
        
        try {
            User user = entityManager.find(User.class, userId.getValue());
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            User user = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public boolean existsById(UserId userId) {
        if (userId == null || userId.getValue() == null) {
            return false;
        }
        
        try {
            Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.id = :id", Long.class)
                .setParameter("id", userId.getValue())
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
    public User find(UserId userId) {
        if (userId == null || userId.getValue() == null) {
            return null;
        }
        
        return entityManager.find(User.class, userId.getValue());
    }
    
    @Override
    public void deleteById(UserId userId) {
        if (userId == null || userId.getValue() == null) {
            return;
        }
        
        User user = entityManager.find(User.class, userId.getValue());
        if (user != null) {
            entityManager.remove(user);
        }
    }
}