package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.LoginStatus;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    /**
     * 기본 생성자
     * loginDate, loginStatus 자동 설정 해줌.
     */
    public MemberLog() {
        this.loginAt = LocalDateTime.now();
        this.loginStatus = LoginStatus.ACTIVE;
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
}
