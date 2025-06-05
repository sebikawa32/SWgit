package com.jose.ticket.domain.ticketinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    List<TicketEntity> findByCategoryId(Long categoryId);

    List<TicketEntity> findByTitleContainingIgnoreCase(String keyword);

    List<TicketEntity> findByTitleContainingIgnoreCaseAndCategoryId(String keyword, Long categoryId);

    // 상세 조회 시 카테고리도 같이 fetch
    @Query("SELECT t FROM TicketEntity t JOIN FETCH t.category WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithCategory(@Param("id") Long id);
}