package com.kusitms.forpet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bookmark_comm")
@NoArgsConstructor
@Getter
@Inheritance(strategy = InheritanceType.JOINED) // 조인 전략
@DiscriminatorColumn // 하위 테이블의 구분 컬럼 생성(default = DTYPE)
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id",unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //==연관관계 메서드==//
    public void setUser(User user) {
        this.user = user;
    }

}