package com.example.scrap.web.category;

import com.example.scrap.entity.Category;
import com.example.scrap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findAllByMemberOrderBySequence(Member member);

    public Optional<Category> findByMemberAndIsDefault(Member member, Boolean isDefault);
}
