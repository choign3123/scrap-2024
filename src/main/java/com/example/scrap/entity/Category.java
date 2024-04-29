package com.example.scrap.entity;

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
public class Category extends BaseEntity {

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

    @Builder
    public Category(String title, int sequence, Member member, Boolean isDefault) {
        this.title = title;
        this.sequence = sequence;
        this.isDefault = isDefault == null ? false : isDefault;

        setMember(member);
    }

    private void setMember(Member member){
        this.member = member;
        member.getCategoryList().add(this);
    }

    public boolean isIllegalMember(Member member){
        return this.member != member;
    }

    public void updateTitle(String title){
        this.title = title;
    }
}
