package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.ScrapStatus;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Boolean favorite;

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

    @Builder
    public Scrap(String scrapURL, String title, String imageURL, String description, String memo, Boolean favorite, ScrapStatus status, Member member, Category category) {
        this.scrapURL = scrapURL;
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.memo = memo;
        this.favorite = favorite == null ? false : favorite;
        this.status = status == null ? ScrapStatus.ACTIVE : status;
        setMember(member);
        setCategory(category);
    }

    private void setMember(Member member){
        this.member = member;
    }

    private void setCategory(Category category){
        this.category = category;
        category.getScrapList().add(this);
    }

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
