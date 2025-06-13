package com.jose.ticket.domain.keyword.repository;

import com.jose.ticket.domain.keyword.entity.PopularKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopularKeywordRepository extends JpaRepository<PopularKeyword, Long> {
    List<PopularKeyword> findTop10ByOrderByCountDesc();
}
