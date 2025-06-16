package com.jose.ticket.domain.bookmark.controller;

import com.jose.ticket.domain.bookmark.dto.BookmarkCreateRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkDeleteRequest;
import com.jose.ticket.domain.bookmark.dto.BookmarkResponse;
import com.jose.ticket.domain.bookmark.service.BookmarkService;
import com.jose.ticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // ✅ 즐겨찾기 추가
    @PostMapping
    public ResponseEntity<?> addBookmark(
            @RequestParam Long ticketId,
            @AuthenticationPrincipal User user
    ) {
        System.out.println("✅ [POST] 즐겨찾기 추가 요청: ticketId = " + ticketId);
        BookmarkCreateRequest request = new BookmarkCreateRequest();
        request.setUserId(user.getId());
        request.setTicketId(ticketId);

        bookmarkService.addBookmark(request);
        return ResponseEntity.ok("즐겨찾기에 추가되었습니다.");
    }

    // ✅ 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<String> removeBookmark(
            @RequestParam Long ticketId,
            @AuthenticationPrincipal User user
    ) {
        System.out.println("🗑️ [DELETE] 즐겨찾기 삭제 요청: ticketId = " + ticketId);
        BookmarkDeleteRequest request = new BookmarkDeleteRequest();
        request.setUserId(user.getId());
        request.setTicketId(ticketId);

        bookmarkService.removeBookmark(request);
        return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
    }

    // ✅ 즐겨찾기 목록 조회
    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> getBookmarks(
            @AuthenticationPrincipal User user
    ) {
        System.out.println("📥 [GET] 즐겨찾기 목록 요청: userId = " + user.getId());
        List<BookmarkResponse> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        return ResponseEntity.ok(bookmarks);
    }

    // ✅ 즐겨찾기 여부 확인 API
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkBookmark(
            @RequestParam Long ticketId,
            @AuthenticationPrincipal User user
    ) {
        boolean exists = bookmarkService.existsBookmark(user.getId(), ticketId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getBookmarkCount(@RequestParam Long ticketId) {
        int count = bookmarkService.getBookmarkCount(ticketId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
