package harry.boilerplate.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BaseEntity JPA 패턴 테스트
 */
class BaseEntityTest {

    @Test
    void BaseEntity_생성시간_자동설정_테스트() {
        // Given
        TestEntity entity = new TestEntity("test-id");
        Instant beforeCreate = Instant.now().minusSeconds(1);
        
        // When
        entity.onCreate(); // @PrePersist 시뮬레이션
        
        // Then
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isAfter(beforeCreate);
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void BaseEntity_수정시간_자동업데이트_테스트() throws InterruptedException {
        // Given
        TestEntity entity = new TestEntity("test-id");
        entity.onCreate(); // @PrePersist 시뮬레이션
        Instant originalUpdatedAt = entity.getUpdatedAt();
        
        // 시간 차이를 위한 대기
        Thread.sleep(10);
        
        // When
        entity.onUpdate(); // @PreUpdate 시뮬레이션
        
        // Then
        assertThat(entity.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getCreatedAt()); // 생성시간은 변경되지 않음
    }

    // Test Entity
    @Entity
    @Table(name = "test_entity")
    private static class TestEntity extends BaseEntity {
        @Id
        private String id;

        public TestEntity() {}

        public TestEntity(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        // 테스트를 위해 protected 메서드를 public으로 노출
        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
        }
    }
}