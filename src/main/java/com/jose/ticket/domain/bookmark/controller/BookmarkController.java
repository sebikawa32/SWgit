package com.jose.ticket.domain.bookmark.controller;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 즐겨찾기 추가
    @PostMapping
    public ResponseEntity<Void> addBookmark(@RequestBody BookmarkCreateRequest request) {
        bookmarkService.addBookmark(request);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@RequestBody BookmarkDeleteRequest request) {
        bookmarkService.removeBookmark(request);
        return ResponseEntity.ok().build();
    }
}

