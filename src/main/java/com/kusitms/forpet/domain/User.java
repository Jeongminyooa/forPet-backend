package com.kusitms.forpet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kusitms.forpet.security.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@NoArgsConstructor
@Getter

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    // 회원가입으로 받게 되는 추가 정보
    private String nickname;
    private String phone;
    private String address;
    private String customImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    //리뷰 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Review> reviewList = new ArrayList<>();

    //북마크(offline-map) 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookmarkPlace> bookmarkPlaceList = new ArrayList<>();

    //북마크(QnaBoard) 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookmarkQna> bookmarkQnaList = new ArrayList<>();

    // 북마크(Community) 참조 관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookmarkComm> bookmarkCommList = new ArrayList<>();

    //백과사전 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<QnaBoard> QnaBoardList = new ArrayList<>();


    //백과사전 댓글 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<CommentQna> commentQnaList = new ArrayList<>();

    // Community 참조관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Community> communityList = new ArrayList<>();

    // 좋아요(Community 참조 관계)
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<LikesComm> likesCommList = new ArrayList<>();

    // 댓글(Community) 참고 관계
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<CommentComm> commentCommList = new ArrayList<>();


    @Builder(builderClassName= "social", builderMethodName = "socialBuilder")
    private User(String name, @Email String email, String imageUrl, Role role) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    @Builder
    private User(Long userId) {
        this.userId = userId;
    }

    @Builder(builderClassName= "role", builderMethodName = "roleBuilder")
    private User(Role role) {
        this.role = role;
    }

    public void updateNameAndImage(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // 권한 업데이트
    public void updateRole(Role role) {
        this.role = role;
    }

    // 커스텀 프로필 이미지 업데이트
    public void updateCustomImage(String customImageUrl) {
        this.customImageUrl = customImageUrl;
    }

    // 동네 등록
    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateNickname(String nickname) { this.nickname = nickname; }

    // 회원가입
    public void signupUser(String nickname, String phone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        // 회원
        this.role = Role.USER;
    }

}
