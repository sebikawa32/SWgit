package com.jose.ticket.domain.bookmark.service;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.entity.Bookmark;
import com.jose.ticket.domain.bookmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    /**즐겨찾기 추가**/
    public void addBookmark(BookmarkCreateRequest request) {
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(request.getUserId());
        bookmark.setTicketId(request.getTicketId());
        bookmarkRepository.save(bookmark);
    }

    /**즐겨찾기 삭제**/
    public void removeBookmark(BookmarkDeleteRequest request) {
        bookmarkRepository.deleteByUserIdAndTicketId(request.getUserId(), request.getTicketId());
    }
}
