package com.jose.ticket.domain.ticketinfo.repository;

import com.jose.ticket.domain.ticketinfo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

