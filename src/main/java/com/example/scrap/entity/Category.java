package com.example.scrap.entity;

import com.example.scrap.base.code.ErrorCode;
import com.example.scrap.base.exception.BaseException;
import com.example.scrap.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity implements Comparable<Category>{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "category")
    private List<Scrap> scrapList = new ArrayList<>();

    /**
     * @param sequence 1 이상
     * @throws IllegalArgumentException sequence가 0 이하의 숫자일 시
     */
    @Builder
    public Category(String title, int sequence, Member member, Boolean isDefault) {
        this.title = title;
        this.isDefault = isDefault == null ? false : isDefault;

        if(sequence <= 0){
            throw new IllegalArgumentException("sequence는 1 이상의 숫자여야 함");
        }
        this.sequence = sequence;

        setMember(member);
    }

    private void setMember(Member member){
        this.member = member;
        member.getCategoryList().add(this);
    }

    @Override
    public int compareTo(Category o) {
        return this.sequence - o.sequence;
    }

    /**
     * 해당 카테고리를 생성한 사용자가 맞는지 확인
     * @param member
     * @throws BaseException ErrorCode.CATEGORY_MEMBER_NOT_MATCH
     */
    public void checkIllegalMember(Member member){
        if(isIllegalMember(member)){
            throw new BaseException(ErrorCode.CATEGORY_MEMBER_NOT_MATCH);
        }
    }

    /**
     * 해당 카테고리를 생성한 사용자가 맞는지 확인
     * @param member
     * @return if match return true, else return false
     */
    public boolean isIllegalMember(Member member){
        return this.member != member;
    }

    public void updateTitle(String title){
        this.title = title;
    }

    /**
     * 카테고리 순서 변경
     * @param sequence
     */
    public void changeSequence(int sequence){
        this.sequence = sequence;
    }

}
