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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final PostRepository postRepository;
    private final TicketRepository ticketRepository;

    // 🔥 티켓 서비스와 똑같이 불용어 리스트 복붙 (통합 관리도 가능)
    private static final Set<String> STOPWORDS = Set.of(
            "의", "이", "가", "은", "는", "을", "를", "에", "에서",
            "와", "과", "도", "으로", "로", "및", "에게", "한테", "께", "께서",
            "밖에", "마저", "까지", "부터", "이나", "나", "라도", "처럼",
            "공연", "콘서트", "전시", "뮤지컬", "연극", "추천"
    );
    // 🔥 키워드 전처리
    public static List<String> splitKeywords(String keyword) {
        String cleaned = keyword.replaceAll("[^\\p{L}\\p{N}\\s]", " ");
        for (String stopword : STOPWORDS) {
            cleaned = cleaned.replace(stopword, " ");
        }
        cleaned = cleaned.replaceAll("\\s+", " ");
        String[] keywords = cleaned.trim().split(" ");
        return Arrays.stream(keywords)
                .filter(s -> !s.isEmpty())
                .toList();
    }
    public List<Board> getBoardsByType(String type) {
        return postRepository.findByType(type);
    }

    public List<Board> getGlobalNotices() {
        return postRepository.findGlobalNoticeByType("notice");
    }

    public List<Board> getBoardsByTicketAndType(Long ticketId, String type) {
        return postRepository.findByTicket_IdAndType(ticketId, type);
    }

    public List<Board> getBoardsByTicket(Long ticketId) {
        return postRepository.findByTicket_Id(ticketId);
    }

    public Board createBoard(BoardRequest request, User writer) {
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

    public Optional<Board> getBoardById(Long id) {
        return postRepository.findById(id);
    }

    public Board updateBoard(Long id, BoardRequest request, User loginUser) {
        Board board = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!board.getWriter().getId().equals(loginUser.getId()) &&
                !loginUser.getRole().equals("ADMIN")) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setType(request.getType());

        if (request.getTicketId() != null) {
            TicketEntity ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new RuntimeException("해당 티켓이 존재하지 않습니다."));
            board.setTicket(ticket);
        } else {
            board.setTicket(null);
        }

        return postRepository.save(board);
    }

    public void deleteBoard(Long id, User loginUser) {
        Board board = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!board.getWriter().getId().equals(loginUser.getId()) &&
                !loginUser.getRole().equals("ADMIN")) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        postRepository.delete(board);
    }

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

    // 🎯 여기만 수정! (AND 검색, 띄어쓰기 무시, 불용어 제거)
    public List<BoardResponse> searchBoards(String query) {
        List<String> keywords = splitKeywords(query);

        if (keywords.isEmpty()) {
            // 키워드가 없으면 전체 반환 (혹은 빈 리스트)
            List<Board> all = postRepository.findAll();
            return all.stream().map(this::toDto).toList();
        }

        List<Board> allBoards = postRepository.findAll();

        List<Board> filtered = allBoards.stream()
                .filter(board -> {
                    // 🔥 특수문자까지 싹 다 제거
                    String title = board.getTitle() != null ? board.getTitle().replaceAll("[^\\p{L}\\p{N}]", "") : "";
                    String content = board.getContent() != null ? board.getContent().replaceAll("[^\\p{L}\\p{N}]", "") : "";

                    return keywords.stream().allMatch(
                            k -> title.contains(k.replaceAll("[^\\p{L}\\p{N}]", "")) ||
                                    content.contains(k.replaceAll("[^\\p{L}\\p{N}]", ""))
                    );
                })
                .toList();

        return filtered.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<BoardResponse> searchBoards(String query, String sort) {
        String pattern = "%" + query.toLowerCase() + "%";
        List<Board> boards;

        if ("views".equals(sort)) {
            boards = postRepository.searchByKeywordOrderByViewCount(pattern);
        } else {
            boards = postRepository.searchByKeyword(pattern);
        }

        return boards.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

    // ✅ 수정된 부분
    public List<BoardResponse> getBoardsByUser(User user) {
        return postRepository.findByWriter(user).stream()
                .map(this::toDto)
                .toList();
    }
}
