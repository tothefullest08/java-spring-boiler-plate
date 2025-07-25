package harry.boilerplate.common.domain;

import org.junit.jupiter.api.Test;

import harry.boilerplate.common.domain.entity.AggregateRoot;
import harry.boilerplate.common.domain.entity.ValueObject;
import harry.boilerplate.common.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 도메인 패턴 기본 동작 검증 테스트
 */
class DomainPatternTest {

    @Test
    void DomainEvent_기본_구현_테스트() {
        // Given
        TestDomainEvent event = new TestDomainEvent("test-aggregate-id");
        
        // Then
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getOccurredAt()).isNotNull();
        assertThat(event.getAggregateId()).isEqualTo("test-aggregate-id");
        assertThat(event.getAggregateType()).isEqualTo("TestAggregate");
        assertThat(event.getVersion()).isEqualTo(1);
    }

    @Test
    void AggregateRoot_도메인이벤트_관리_테스트() {
        // Given
        TestAggregateRoot aggregate = new TestAggregateRoot("test-id");
        TestDomainEvent event = new TestDomainEvent("test-id");
        
        // When
        aggregate.addTestEvent(event);
        
        // Then
        assertThat(aggregate.getDomainEvents()).hasSize(1);
        assertThat(aggregate.getDomainEvents().get(0)).isEqualTo(event);
        
        // When
        aggregate.clearDomainEvents();
        
        // Then
        assertThat(aggregate.getDomainEvents()).isEmpty();
    }

    @Test
    void ValueObject_동등성_비교_테스트() {
        // Given
        TestValueObject vo1 = new TestValueObject("test", 100);
        TestValueObject vo2 = new TestValueObject("test", 100);
        TestValueObject vo3 = new TestValueObject("test", 200);
        
        // Then
        assertThat(vo1).isEqualTo(vo2);
        assertThat(vo1).isNotEqualTo(vo3);
        assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
    }

    // Test implementations
    private static class TestDomainEvent implements DomainEvent {
        private final UUID eventId = UUID.randomUUID();
        private final Instant occurredAt = Instant.now();
        private final String aggregateId;
        private final String aggregateType = "TestAggregate";
        private final int version = 1;

        public TestDomainEvent(String aggregateId) {
            this.aggregateId = aggregateId;
        }

        @Override
        public UUID getEventId() { return eventId; }

        @Override
        public Instant getOccurredAt() { return occurredAt; }

        @Override
        public String getAggregateId() { return aggregateId; }

        @Override
        public String getAggregateType() { return aggregateType; }

        @Override
        public int getVersion() { return version; }
    }

    private static class TestAggregateRoot extends AggregateRoot<TestAggregateRoot, String> {
        private final String id;

        public TestAggregateRoot(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public void addTestEvent(DomainEvent event) {
            addDomainEvent(event);
        }
    }

    private static class TestValueObject extends ValueObject {
        private final String name;
        private final int value;

        public TestValueObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        protected boolean equalsByValue(Object other) {
            if (!(other instanceof TestValueObject)) return false;
            TestValueObject that = (TestValueObject) other;
            return this.name.equals(that.name) && this.value == that.value;
        }

        @Override
        protected Object[] getEqualityComponents() {
            return new Object[]{name, value};
        }
    }
}