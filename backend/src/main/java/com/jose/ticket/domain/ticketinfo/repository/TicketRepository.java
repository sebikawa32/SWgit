package com.jose.ticket.domain.ticketinfo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    List<TicketEntity> findByCategoryId(int categoryId);
    Page<TicketEntity> findByCategoryId(int categoryId, Pageable pageable);
    List<TicketEntity> findByTitleContainingIgnoreCaseAndCategoryId(String keyword, Integer categoryId);

    @Query("SELECT t FROM TicketEntity t WHERE (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<TicketEntity> searchByTitleOnly(@Param("keyword") String keyword);

    @Query("SELECT t FROM TicketEntity t JOIN FETCH t.category WHERE t.id = :id")
    Optional<TicketEntity> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT t FROM TicketEntity t WHERE t.category.id = :categoryId AND t.eventEndDatetime >= CURRENT_DATE ORDER BY t.eventStartDatetime ASC")
    List<TicketEntity> findUpcomingTicketsByCategory(@Param("categoryId") int categoryId);

    @Query("SELECT t FROM TicketEntity t WHERE t.category.id = :categoryId AND t.eventEndDatetime >= CURRENT_DATE")
    Page<TicketEntity> findUpcomingTicketsByCategoryPaged(@Param("categoryId") int categoryId, Pageable pageable);

}
