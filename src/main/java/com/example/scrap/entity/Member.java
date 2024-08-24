package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.SnsType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SnsType snsType;

    @Column(length = 45, nullable = false)
    private String snsId;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "member_log_id", nullable = false)
    private MemberLog memberLog;

    @OneToMany(mappedBy = "member")
    private List<Category> categoryList = new ArrayList<>();

    @Builder
    public Member(String name, SnsType snsType, String snsId, MemberLog memberLog) {
        this.name = name;
        this.snsType = snsType;
        this.snsId = snsId;
        this.memberLog = memberLog;
    }

    // 삭제된 카테고리도 포함하고 있기 때문에, 함부러 접근할 수 없도록 막음
    protected List<Category> getCategoryList(){
        return this.categoryList;
    }

    /**
     * 로그인
     */
    public void login(){
        this.getMemberLog().login();
    }

    /**
     * 로그아웃
     */
    public void logout() {
        this.getMemberLog().logout();
    }

    /**
     * refreshTokenId 갱신
     */
    public void setRefreshTokenId(Long refreshTokenId){
        memberLog.setRefreshTokenId(refreshTokenId);
    }
}
