package com.jose.ticket.domain.bookmark.controller;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 즐겨찾기 추가 요청 처리
    @PostMapping
    public ResponseEntity<String> addBookmark(@RequestBody BookmarkCreateRequest request) {
        try {
            bookmarkService.addBookmark(request);
            return ResponseEntity.ok("즐겨찾기에 등록되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 즐겨찾기 삭제 요청 처리
    @DeleteMapping
    public ResponseEntity<String> removeBookmark(@RequestBody BookmarkDeleteRequest request) {
        try {
            bookmarkService.removeBookmark(request);
            return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
