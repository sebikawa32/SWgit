package com.jose.ticket.domain.bookmark.controller;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkResponse;
import com.jose.ticket.domain.bookmark.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    // 즐겨찾기 추가
    @PostMapping
    public ResponseEntity<String> addBookmark(@RequestBody BookmarkCreateRequest request) {
        try {
            bookmarkService.addBookmark(request);
            return ResponseEntity.ok("즐겨찾기에 추가되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());  // 중복 에러 등
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());  // 티켓 없음 에러 등
        }
    }

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<String> removeBookmark(@RequestBody BookmarkDeleteRequest request) {
        try {
            bookmarkService.removeBookmark(request);
            return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());  // 없는 즐겨찾기 삭제 시도 등
        }
    }

    //즐겨찾기 목록조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<BookmarkResponse>> getBookmarks(@PathVariable Long userId) {
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }
}

