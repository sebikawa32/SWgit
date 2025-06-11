package com.jose.ticket.domain.clicklog.repository;

import com.jose.ticket.domain.clicklog.entity.ClickLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickLogRepository extends JpaRepository<ClickLogEntity, Long> {
}
