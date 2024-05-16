package com.example.scrap.web.scrap;

import com.example.scrap.entity.Member;
import com.example.scrap.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long>, JpaSpecificationExecutor<Scrap>{

}
