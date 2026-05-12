package com.iot.repositories;

import com.iot.models.entities.Metric;
import com.iot.models.enums.MetricType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA access for {@link Metric}.
 *
 * <p>Inherited {@code save}, {@code findAll}, {@code findById}, etc. are available out of the box.
 * Custom queries below support algorithm output retrieval and frontend visualization.
 */
public interface MetricRepository extends JpaRepository<Metric, Long> {

    /**
     * Returns all metrics of a given type ordered by most recent first.
     * Used to display algorithm results in the frontend.
     */
    List<Metric> findByTypeOrderByCreatedAtDesc(MetricType type);

    /**
     * Returns the most recent metric of a given type.
     * Used for live dashboard display of latest algorithm output.
     */
    Optional<Metric> findTopByTypeOrderByCreatedAtDesc(MetricType type);

    /**
     * Returns all metrics of a given type recorded between two timestamps, ordered ascending.
     * Used for historical chart visualization.
     */
    List<Metric> findByTypeAndCreatedAtBetweenOrderByCreatedAtAsc(MetricType type, Instant from, Instant to);
}