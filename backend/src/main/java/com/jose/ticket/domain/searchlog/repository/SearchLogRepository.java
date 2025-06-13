package com.jose.ticket.domain.searchlog.repository;

import com.jose.ticket.domain.searchlog.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
}
