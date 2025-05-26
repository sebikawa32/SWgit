package com.jose.ticket.domain.category.repository;

import com.jose.ticket.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

