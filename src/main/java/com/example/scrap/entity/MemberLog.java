package com.example.scrap.entity;

import com.example.scrap.base.data.DefaultData;
import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.LoginStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
public class MemberLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime loginAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginStatus loginStatus;

    @Column(nullable = false)
    private Long refreshTokenId;

    /**
     * 기본 생성자
     * loginDate, loginStatus 자동 설정 해줌.
     */
    public MemberLog() {
        this.loginAt = LocalDateTime.now();
        this.loginStatus = LoginStatus.ACTIVE;
        this.refreshTokenId = 0L;
    }

    /**
     * 로그인
     */
    public void login(){
        this.loginAt = LocalDateTime.now();
        this.loginStatus = LoginStatus.ACTIVE;
    }

    /**
     * 로그아웃
     */
    public void logout(){
        this.loginStatus = LoginStatus.LOGOUT;
    }

    /**
     * refreshTokenId 업데이트
     * @throws IllegalArgumentException 기존과 똑같은 값으로 수정하려는 경우
     */
    // TODO: null 체크 로직 추가하기 (2)
    public void setRefreshTokenId(Long refreshTokenId){
        if(this.refreshTokenId == null || !this.refreshTokenId.equals(refreshTokenId)){
            this.refreshTokenId = refreshTokenId;
        }
        else{
            throw new IllegalArgumentException("기존과 똑같은 값으로는 수정 불가능");
        }
    }

    /**
     * 기존과 다른 refreshTokenId 생성
     */
    public Long createRefreshTokenId(){
        Random random = new Random();
        Long refreshTokenId = random.nextLong(DefaultData.MIN_REFRESH_TOKEN_ID, DefaultData.MAX_REFRESH_TOKEN_ID);
        while(refreshTokenId.equals(this.refreshTokenId)){
            refreshTokenId = random.nextLong(DefaultData.MIN_REFRESH_TOKEN_ID, DefaultData.MAX_REFRESH_TOKEN_ID);
        }

        return refreshTokenId;
    }

    public boolean equalRefreshTokenId(Long refreshTokenId){
        return this.refreshTokenId.equals(refreshTokenId);
    }
}
