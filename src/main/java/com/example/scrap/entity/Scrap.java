package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.ScrapStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scrap_url", length = 500, nullable = false)
    private String scrapURL;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_url", length = 500)
    private String imageURL;

    private String description;

    @Lob
    private String memo;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean star;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ACTIVE'")
    private ScrapStatus status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
}
