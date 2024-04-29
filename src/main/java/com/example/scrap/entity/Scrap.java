package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.ScrapStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    /**
     * 스크랩 휴지통 보내기
     */
    public void toTrash(){
        this.category = null;
        this.status = ScrapStatus.TRASH;
    }

    /**
     * 카테고리 이동하기
     * @param category
     */
    public void moveCategory(Category category){
        if(this.category != null){
            this.category.getScrapList().remove(this);
        }

        this.category = category;
        category.getScrapList().add(this);
    }
}
