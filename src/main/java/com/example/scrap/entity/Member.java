package com.example.scrap.entity;

import com.example.scrap.entity.base.BaseEntity;
import com.example.scrap.entity.enums.SnsType;
import lombok.AccessLevel;
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

    @OneToOne(optional = false)
    @JoinColumn(name = "member_log_id")
    private MemberLog memberLog;

    @OneToMany(mappedBy = "member")
    private List<Category> categoryList = new ArrayList<>();

    public int calcNewCategorySequence(){
        return categoryList.size() + 1;
    }
}
