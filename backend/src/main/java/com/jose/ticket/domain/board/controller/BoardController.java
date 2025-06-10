package com.jose.ticket.domain.board.controller;

import com.jose.ticket.domain.board.dto.BoardRequest;
import com.jose.ticket.domain.board.dto.BoardResponse;
import com.jose.ticket.domain.board.entity.Board;
import com.jose.ticket.domain.board.service.BoardService;
import com.jose.ticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    // ✅ 게시글 전체 조회 (type: general 또는 notice), 공지사항의 경우 global=true 처리
    @GetMapping("")
    public ResponseEntity<List<BoardResponse>> getBoardsByType(
            @RequestParam(defaultValue = "general") String type,
            @RequestParam(required = false, defaultValue = "false") boolean global,
            @RequestParam(defaultValue = "latest") String sort // ✅ 정렬 기준 파라미터 추가
    ) {
        System.out.println("📨 [BoardController] type = " + type + ", global = " + global + ", sort = " + sort);

        List<BoardResponse> response;

        if (global && type.equals("notice")) {
            response = boardService.getGlobalNotices()
                    .stream()
                    .map(boardService::toDto)
                    .toList();
        } else {
            response = boardService.searchBoardsByTypeAndSort(type, sort);
        }

        return ResponseEntity.ok(response);
    }

    // ✅ 특정 티켓에 대한 게시글 목록 조회 (type 포함)
    @GetMapping("/tickets/{ticketId}/boards")
    public ResponseEntity<List<BoardResponse>> getBoardsByTicketAndType(
            @PathVariable Long ticketId,
            @RequestParam(defaultValue = "general") String type
    ) {
        List<Board> boards = boardService.getBoardsByTicketAndType(ticketId, type);
        List<BoardResponse> responses = boards.stream()
                .map(boardService::toDto)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ✅ 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id)
                .map(boardService::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 게시글 생성
    @PostMapping("")
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request,
                                                     @AuthenticationPrincipal User user) {
        System.out.println("🔐 BoardController: 인증된 사용자 = " + (user != null ? user.getId() : "null"));
        System.out.println("🔐 사용자 클래스 = " + (user != null ? user.getClass().getName() : "null"));

        if (user == null) {
            throw new RuntimeException("❌ 인증된 사용자가 없습니다.");
        }

        Board saved = boardService.createBoard(request, user);
        return ResponseEntity.ok(boardService.toDto(saved));
    }

    // ✅ 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable Long id,
                                             @RequestBody Board updatedBoard) {
        Board board = boardService.updateBoard(id, updatedBoard);
        return ResponseEntity.ok(board);
    }

    // ✅ 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
