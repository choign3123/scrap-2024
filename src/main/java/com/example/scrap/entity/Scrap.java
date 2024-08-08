package com.example.scrap.entity;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.ScrapStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Boolean isFavorite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ACTIVE'")
    private ScrapStatus status;

    private LocalDateTime trashedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Scrap(String scrapURL, String title, String imageURL, String description, String memo, Boolean isFavorite, ScrapStatus status, Member member, Category category) {
        this.scrapURL = scrapURL;
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.memo = memo;
        this.isFavorite = isFavorite == null ? false : isFavorite;
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
     * 해당 사용자가 만든 스크랩이 맞는지 확인
     * @param member
     * @return if match return true, else throw BaseException
     * @throws BaseException ErrorCode.SCRAP_MEMBER_NOT_MATCH
     */
    public boolean checkIllegalMember(Member member){
        if(isIllegalMember(member)){
            throw new BaseException(ErrorCode.SCRAP_MEMBER_NOT_MATCH);
        }

        return true;
    }

    /**
     * 해당 사용자가 만든 스크랩이 맞는지 확인
     * @param member
     * @return if match return true, else return false
     */
    public boolean isIllegalMember(Member member){
        return this.member != member;
    }

    /**
     * 해당 카테고리에 속한 스크랩이 맞는지 확인
     * @param category
     * @return if match return true, else throw BaseException
     * @throws BaseException ErrorCode.SCRAP_MEMBER_NOT_MATCH
     */
    public boolean checkIllegalCategory(Category category){
        if(isIllegalCategory(category)){
            throw new BaseException(ErrorCode.SCRAP_CATEGORY_NOT_MATCH);
        }

        return true;
    }

    /**
     * 해당 사용자가 만든 스크랩이 맞는지 확인
     * @param category
     * @return if match return true, else return false
     */
    public boolean isIllegalCategory(Category category){
        return this.category != category;
    }

    /**
     * 스크랩 유효성 확인
     * @return if status is ACTIVE return true, else return false.
     */
    public boolean isAvailable(){
        return status == ScrapStatus.ACTIVE;
    }

    /**
     * 스크랩 휴지통 보내기
     */
    public void toTrash(){
        this.status = ScrapStatus.TRASH;
        trashedAt = LocalDateTime.now();
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

    /**
     * 즐겨찾기
     * @return true=즐겨찾기 됨. false=즐겨찾기 해제
     */
    public boolean toggleFavorite(){
        isFavorite = !isFavorite;
        return isFavorite;
    }

    /**
     * 즐겨찾기
     * @param isFavorite true=즐겨찾기 됨. false=즐겨찾기 해제
     * @return
     */
    public boolean toggleFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
        return this.isFavorite;
    }

    /**
     * 메모 수정
     * @param memo
     */
    public void updateMemo(String memo){
        this.memo = memo;
    }

}
