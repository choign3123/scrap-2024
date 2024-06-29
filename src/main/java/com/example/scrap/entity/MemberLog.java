package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.LoginStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime loginDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginStatus loginStatus;

    @Builder
    public MemberLog(LocalDateTime loginDate, LoginStatus loginStatus) {
        this.loginDate = loginDate;
        this.loginStatus = loginStatus;
    }

    /**
     * 로그인
     */
    public void login(){
        this.loginDate = LocalDateTime.now();
        this.loginStatus = LoginStatus.ACTIVE;
    }

    /**
     * 로그아웃
     */
    public void logout(){
        this.loginStatus = LoginStatus.LOGOUT;
    }
}
