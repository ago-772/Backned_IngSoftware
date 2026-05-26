package com.iot.repositories;

import com.iot.models.entities.EventEntity;
import com.iot.models.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    
    //Returns all events ordered by timestamp ascending.
    List<EventEntity> findByTypeOrderByTimestampDesc(EventType type);

    /**
     * Returns all events recorded between two timestamps, ordered ascending.
     * Used for historical queries and session reconstruction.
     */
    List<EventEntity> findByTimestampBetweenOrderByTimestampAsc(Instant from, Instant to);

    //Returns the total count of events of a given type.
    long countByType(EventType type);
    
    //Returns the most recent event of a given type.
    Optional<EventEntity> findTopByTypeOrderByTimestampDesc(EventType type);
}