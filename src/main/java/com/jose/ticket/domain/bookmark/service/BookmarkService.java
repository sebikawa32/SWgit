package com.jose.ticket.domain.bookmark.service;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkResponse;
import com.jose.ticket.domain.bookmark.entity.Bookmark;
import com.jose.ticket.domain.bookmark.repository.BookmarkRepository;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final TicketRepository ticketRepository; // 추가된 부분

    public BookmarkService(BookmarkRepository bookmarkRepository, TicketRepository ticketRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.ticketRepository = ticketRepository; // 주입
    }

    /** 즐겨찾기 추가
     - 중복 여부 확인 후 신규 즐겨찾기 생성 및 저장 */
    public void addBookmark(BookmarkCreateRequest request) {
        // 중복 체크
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndTicketId(request.getUserId(), request.getTicketId());
        if (existing.isPresent()) {
            throw new IllegalStateException("이미 즐겨찾기에 등록되어 있습니다.");
        }

        // ticketId로 TicketEntity 조회
        TicketEntity ticketEntity = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓을 찾을 수 없습니다."));

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(request.getUserId());
        bookmark.setTicket(ticketEntity); // ticket 설정
        bookmarkRepository.save(bookmark);
    }

    /** 즐겨찾기 삭제
     - 요청한 즐겨찾기가 존재하는지 확인 후 삭제 처리 */
    public void removeBookmark(BookmarkDeleteRequest request) {
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndTicketId(request.getUserId(), request.getTicketId());
        if (existing.isEmpty()) {
            throw new IllegalStateException("즐겨찾기에 없는 항목입니다.");
        }
        bookmarkRepository.delete(existing.get());
    }

    // 유저가 등록한 즐겨찾기 목록 조회
     public List<BookmarkResponse> getBookmarksByUserId(Long userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByUserId(userId);

        // Bookmark 엔티티를 DTO로 변환해서 반환
         return bookmarks.stream()
                 .map(bookmark -> {
                     BookmarkResponse response = new BookmarkResponse();
                     response.setBookmarkId(bookmark.getBookmarkId());
                     response.setTicketId(bookmark.getTicket().getId());
                     response.setTicketTitle(bookmark.getTicket().getTitle());
                     response.setVenue(bookmark.getTicket().getVenue());  // 추가
                     response.setEventDatetime(bookmark.getTicket().getEventDatetime());  // 추가
                     response.setImageUrl(bookmark.getTicket().getImageUrl());  // 추가
                     response.setCreatedAt(bookmark.getBookmarkCreatedAt());
                     return response;
                 })
                 .collect(Collectors.toList());
     }
}
