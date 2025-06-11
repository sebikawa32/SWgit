package com.jose.ticket.domain.popular.repository;

import com.jose.ticket.domain.popular.entity.TicketPopularity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PopularityRepository extends JpaRepository<TicketPopularity, Long> {
    List<TicketPopularity> findAllByOrderByPopularityScoreDesc();
}
