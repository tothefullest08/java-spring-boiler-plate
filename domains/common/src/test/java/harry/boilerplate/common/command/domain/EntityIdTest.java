package harry.boilerplate.common.command.domain;

import org.junit.jupiter.api.Test;

import harry.boilerplate.common.domain.entity.EntityId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EntityId 값 객체 테스트
 */
class EntityIdTest {

    @Test
    void EntityId_UUID_자동생성_테스트() {
        // Given & When
        TestEntityId id1 = new TestEntityId();
        TestEntityId id2 = new TestEntityId();
        
        // Then
        assertThat(id1.getValue()).isNotNull();
        assertThat(id1.getValue()).hasSize(36); // UUID 길이
        assertThat(id1.getValue()).isNotEqualTo(id2.getValue()); // 각각 다른 UUID
    }

    @Test
    void EntityId_값_지정_생성_테스트() {
        // Given
        String testValue = "test-id-123";
        
        // When
        TestEntityId id = new TestEntityId(testValue);
        
        // Then
        assertThat(id.getValue()).isEqualTo(testValue);
    }

    @Test
    void EntityId_동등성_테스트() {
        // Given
        String testValue = "test-id-123";
        TestEntityId id1 = new TestEntityId(testValue);
        TestEntityId id2 = new TestEntityId(testValue);
        TestEntityId id3 = new TestEntityId("different-id");
        
        // Then
        assertThat(id1).isEqualTo(id2);
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void EntityId_null_값_예외_테스트() {
        // Then
        assertThatThrownBy(() -> new TestEntityId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ID value cannot be null or empty");
    }

    @Test
    void EntityId_빈_값_예외_테스트() {
        // Then
        assertThatThrownBy(() -> new TestEntityId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ID value cannot be null or empty");
            
        assertThatThrownBy(() -> new TestEntityId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("ID value cannot be null or empty");
    }

    @Test
    void EntityId_toString_테스트() {
        // Given
        String testValue = "test-id-123";
        TestEntityId id = new TestEntityId(testValue);
        
        // When & Then
        assertThat(id.toString()).isEqualTo(testValue);
    }

    // Test implementation
    private static class TestEntityId extends EntityId {
        public TestEntityId() {
            super();
        }
        
        public TestEntityId(String value) {
            super(value);
        }
    }
}