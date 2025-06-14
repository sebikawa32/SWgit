package com.jose.ticket.domain.board.service;

import com.jose.ticket.domain.board.dto.BoardRequest;
import com.jose.ticket.domain.board.dto.BoardResponse;
import com.jose.ticket.domain.board.entity.Board;
import com.jose.ticket.domain.board.repository.PostRepository;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import com.jose.ticket.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final PostRepository postRepository;
    private final TicketRepository ticketRepository;

    // ✅ 타입별 게시글 조회 (공지 or 일반)
    public List<Board> getBoardsByType(String type) {
        return postRepository.findByType(type);
    }

    // ✅ 전체 공지사항만 조회 (ticket과 무관한 notice)
    public List<Board> getGlobalNotices() {
        return postRepository.findGlobalNoticeByType("notice");
    }

    // ✅ 특정 티켓에 연결된 게시글 조회 (공지 or 일반)
    public List<Board> getBoardsByTicketAndType(Long ticketId, String type) {
        return postRepository.findByTicket_IdAndType(ticketId, type);
    }

    // ✅ 특정 티켓에 연결된 모든 게시글 조회 (공지 + 일반)
    public List<Board> getBoardsByTicket(Long ticketId) {
        return postRepository.findByTicket_Id(ticketId);
    }

    // ✅ 게시글 생성
    public Board createBoard(BoardRequest request, User writer) {
        // ✨ 공지사항은 관리자만 작성 가능
        if ("notice".equalsIgnoreCase(request.getType()) && !writer.getRole().equals("ADMIN")) {
            throw new SecurityException("공지사항은 관리자만 작성할 수 있습니다.");
        }

        TicketEntity ticket = null;
        if (request.getTicketId() != null) {
            ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new RuntimeException("해당 티켓이 존재하지 않습니다."));
        }

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(writer)
                .ticket(ticket)
                .type(request.getType())
                .build();

        Board saved = postRepository.save(board);
        saved.setWriter(writer); // lazy 방지
        return saved;
    }

    // ✅ 게시글 ID로 조회
    public Optional<Board> getBoardById(Long id) {
        return postRepository.findById(id);
    }

    // ✅ 게시글 수정
    public Board updateBoard(Long id, BoardRequest request, User loginUser) {
        Board board = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자나 관리자만 수정 가능
        if (!board.getWriter().getId().equals(loginUser.getId()) &&
                !loginUser.getRole().equals("ADMIN")) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setType(request.getType());

        // ✅ ticketId가 포함되어 있다면 ticket 정보도 업데이트
        if (request.getTicketId() != null) {
            TicketEntity ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new RuntimeException("해당 티켓이 존재하지 않습니다."));
            board.setTicket(ticket);
        } else {
            board.setTicket(null); // 연결 해제
        }

        return postRepository.save(board);
    }


    // ✅ 게시글 삭제
    public void deleteBoard(Long id, User loginUser) {
        Board board = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!board.getWriter().getId().equals(loginUser.getId()) &&
                !loginUser.getRole().equals("ADMIN")) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        postRepository.delete(board);
    }

    // ✅ Board → DTO 변환
    public BoardResponse toDto(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .type(board.getType())
                .writerId(board.getWriter().getId())
                .nickname(board.getWriter().getNickname())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .ticketId(board.getTicket() != null ? board.getTicket().getId() : null)
                .ticketTitle(board.getTicket() != null ? board.getTicket().getTitle() : null)
                .build();
    }

    // ✅ 검색 기능 (writer JOIN 포함)
    public List<BoardResponse> searchBoards(String query) {
        String pattern = "%" + query.toLowerCase() + "%";
        List<Board> boards = postRepository.searchByKeyword(pattern);
        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BoardResponse> searchBoards(String query, String sort) {
        String pattern = "%" + query.toLowerCase() + "%";
        List<Board> boards;

        if ("views".equals(sort)) {
            boards = postRepository.searchByKeywordOrderByViewCount(pattern);
        } else {
            boards = postRepository.searchByKeyword(pattern); // 기본 최신순 정렬
        }

        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ✅ 타입 + 정렬 기준에 따라 게시글 조회
    public List<BoardResponse> searchBoardsByTypeAndSort(String type, String sort) {
        List<Board> boards;

        if ("views".equals(sort)) {
            boards = postRepository.findByTypeOrderByViewCountDesc(type);
        } else {
            boards = postRepository.findByTypeOrderByCreatedAtDesc(type);
        }

        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
