package com.jose.ticket.domain.clicklog.service;

import com.jose.ticket.domain.clicklog.entity.ClickLogEntity;
import com.jose.ticket.domain.clicklog.repository.ClickLogRepository;
import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;
import com.jose.ticket.domain.ticketinfo.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogRepository clickLogRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public void logClick(Long ticketId, Long userId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
        ClickLogEntity clickLog = new ClickLogEntity();
        clickLog.setTicket(ticket);
        clickLog.setUserId(userId); // 비회원이면 null로 전달
        clickLogRepository.save(clickLog);
    }
}
