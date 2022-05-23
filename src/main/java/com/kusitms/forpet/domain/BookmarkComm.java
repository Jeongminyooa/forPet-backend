package com.kusitms.forpet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bookmark_comm")
@NoArgsConstructor
@Getter
public class BookmarkComm extends Bookmark{

    //커뮤니티 참조관계
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Community community;

    //==연관관계 메서드==//
    public void setUser(User user) {
        this.setUser(user);
        user.getBookmarkCommList().add(this);
    }

    public void setCommunity(Community community) {
        this.community = community;
        community.getBookmarkCommList().add(this);
    }

}