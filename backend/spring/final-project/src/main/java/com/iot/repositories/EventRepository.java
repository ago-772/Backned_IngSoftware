package com.iot.repositories;

import com.iot.models.entities.EventEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

  // Returns the most recent event of a given type.
  Optional<EventEntity> findTopByOrderByTimestampDesc();
}
