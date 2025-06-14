package com.jose.ticket.domain.ticketinfo.repository;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Modifying
    @Transactional
    @Query("UPDATE TicketEntity t SET t.viewCount = t.viewCount + 1 WHERE t.id = :ticketId")
    void increaseViewCount(@Param("ticketId") Long ticketId);

    // ✅ 유연한 대화형 필터 검색 쿼리 (LocalDateTime 버전)
    @Query("""
        SELECT t FROM TicketEntity t
        WHERE (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:venue IS NULL OR t.venue LIKE CONCAT('%', :venue, '%'))
          AND (:priceMax IS NULL OR 
               CAST(FUNCTION('REPLACE', t.price, '원', '') AS integer) <= :priceMax)
          AND (
            (:startDate IS NULL AND :endDate IS NULL)
            OR (:startDate IS NOT NULL AND :endDate IS NOT NULL AND t.eventStartDatetime <= :endDate AND t.eventEndDatetime >= :startDate)
            OR (:startDate IS NOT NULL AND :endDate IS NULL AND t.eventEndDatetime >= :startDate)
            OR (:startDate IS NULL AND :endDate IS NOT NULL AND t.eventStartDatetime <= :endDate)
          )
          AND (:title IS NULL OR t.title LIKE CONCAT('%', :title, '%'))
          AND (:ageLimit IS NULL OR t.ageLimit LIKE CONCAT('%', :ageLimit, '%'))
          AND (:bookingProvider IS NULL OR t.bookingProvider = :bookingProvider)
    """)
    List<TicketEntity> findByDynamicFilter(
            @Param("categoryId") Integer categoryId,
            @Param("venue") String venue,
            @Param("priceMax") Integer priceMax,
            @Param("startDate") LocalDateTime startDate, // ✅ 변경됨
            @Param("endDate") LocalDateTime endDate,     // ✅ 변경됨
            @Param("title") String title,
            @Param("ageLimit") String ageLimit,
            @Param("bookingProvider") String bookingProvider
    );
}
