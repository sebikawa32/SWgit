package com.jose.ticket.domain.ticketinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.ticket.domain.ticketinfo.entity.TicketEntity;

// TicketInfo 엔티티를 조작하는 JPA 리포지토리
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
    // JpaRepository 덕분에 findAll(), save(), findById() 등 메서드를 자동으로 쓸 수 있음

}
