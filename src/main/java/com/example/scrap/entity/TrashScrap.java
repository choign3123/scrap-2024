package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TrashScrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Scrap 데이터 시작 */
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
    private Boolean isFavorite;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;
    /* Scrap 데이터 끝 */

    @CreatedDate
    private LocalDateTime trashedAt;

    public TrashScrap(Scrap scrap){
        this.scrapURL = scrap.getScrapURL();
        this.title = scrap.getTitle();
        this.imageURL = scrap.getImageURL();
        this.description = scrap.getDescription();
        this.memo = scrap.getMemo();
        this.isFavorite = scrap.getIsFavorite();
        setMember(scrap.getMember());
        setCategory(scrap.getCategory());
    }

    private void setMember(Member member){
        this.member = member;
    }

    private void setCategory(Category category){
        this.category = category;
    }
}
