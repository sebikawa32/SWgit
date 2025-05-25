package com.jose.ticket.domain.bookmark.service;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.entity.Bookmark;
import com.jose.ticket.domain.bookmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    /** 즐겨찾기 추가
     - 중복 여부 확인 후 신규 즐겨찾기 생성 및 저장 */
    public void addBookmark(BookmarkCreateRequest request) {
        // 중복 체크
        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndTicketId(request.getUserId(), request.getTicketId());
        if (existing.isPresent()) {
            throw new IllegalStateException("이미 즐겨찾기에 등록되어 있습니다.");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(request.getUserId());
        bookmark.setTicketId(request.getTicketId());
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
}
