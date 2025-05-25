package com.jose.ticket.domain.ticketinfo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jose.ticket.domain.ticketinfo.dto.TicketResponseDto;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service // 비즈니스 로직을 처리하는 서비스 클래스임을 나타냄
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // DB에서 모든 티켓을 가져와서 DTO 리스트로 변환
    public List<TicketResponseDto> getAllTickets() {
        List<TicketEntity> all = ticketRepository.findAll(); // JPA 기본 메서드
        return all.stream().map(TicketResponseDto::new) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }
}