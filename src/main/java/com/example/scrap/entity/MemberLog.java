package com.example.scrap.entity;

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
public class MemberLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime loginDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginStatus loginStatus;

    private LocalDateTime unregisterDate;

    @Builder
    public MemberLog(LocalDateTime loginDate, LoginStatus loginStatus) {
        this.loginDate = loginDate;
        this.loginStatus = loginStatus;
    }

    public void setLoginDate(LocalDateTime loginDate){
        this.loginDate = loginDate;
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

    /**
     * 회원탈퇴
     */
    public void signOut(){
        this.unregisterDate = LocalDateTime.now();
        this.loginStatus = LoginStatus.UNREGISTER;
    }
}
