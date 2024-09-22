package com.example.scrap.web.scrap;

import com.example.scrap.entity.TrashScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrashScrapRepository extends JpaRepository<TrashScrap, Long> {
}
