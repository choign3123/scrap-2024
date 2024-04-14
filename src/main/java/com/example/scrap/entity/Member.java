package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.MemberStatus;
import com.example.scrap.entity.enums.SocialType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'ACTIVE'")
    private MemberStatus status;

    private Date unregisterDate;

    @OneToMany(mappedBy = "member")
    private List<Category> categoryList = new ArrayList<>();
}
