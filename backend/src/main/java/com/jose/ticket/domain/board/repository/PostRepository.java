package com.jose.ticket.domain.board.repository;

import com.jose.ticket.domain.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b JOIN FETCH b.writer")
    List<Board> findAllWithWriter();

    @Query("SELECT b FROM Board b JOIN FETCH b.writer WHERE b.type = :type")
    List<Board> findByType(@Param("type") String type);

    @Query("SELECT b FROM Board b JOIN FETCH b.writer WHERE b.type = :type AND b.ticket IS NULL")
    List<Board> findGlobalNoticeByType(@Param("type") String type);

    List<Board> findByTicket_Id(Long ticketId);

    List<Board> findByTicket_IdAndType(Long ticketId, String type);

    // ✅ Hibernate 6 완전 대응: LOWER는 엔티티 필드에만 사용, 파라미터는 그대로
    @Query(
            value = """

                    SELECT b.* FROM board b 
    LEFT JOIN ticket t ON b.ticket_id = t.ticket_id
    WHERE 
        (b.title IS NOT NULL AND LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')))
        OR 
        (b.content IS NOT NULL AND LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%')))
        OR
        (t.ticket_title IS NOT NULL AND LOWER(t.ticket_title) LIKE LOWER(CONCAT('%', :query, '%')))
    """,
            nativeQuery = true
    )
    List<Board> searchByKeyword(@Param("query") String query);

    @Query("""
  SELECT b FROM Board b
  LEFT JOIN FETCH b.writer
  ORDER BY b.viewCount DESC
""")
    List<Board> findAllOrderByViewCountDesc();

    @Query(
            value = """
        SELECT b.* FROM board b 
        LEFT JOIN ticket t ON b.ticket_id = t.ticket_id
        WHERE 
            (b.title IS NOT NULL AND LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')))
            OR 
            (b.content IS NOT NULL AND LOWER(b.content) LIKE LOWER(CONCAT('%', :query, '%')))
            OR
            (t.ticket_title IS NOT NULL AND LOWER(t.ticket_title) LIKE LOWER(CONCAT('%', :query, '%')))
        ORDER BY b.view_count DESC
    """,
            nativeQuery = true
    )
    List<Board> searchByKeywordOrderByViewCount(@Param("query") String query);


    @Query("SELECT b FROM Board b JOIN FETCH b.writer WHERE b.type = :type ORDER BY b.createdAt DESC")
    List<Board> findByTypeOrderByCreatedAtDesc(@Param("type") String type);

    @Query("SELECT b FROM Board b JOIN FETCH b.writer WHERE b.type = :type ORDER BY b.viewCount DESC")
    List<Board> findByTypeOrderByViewCountDesc(@Param("type") String type);
}