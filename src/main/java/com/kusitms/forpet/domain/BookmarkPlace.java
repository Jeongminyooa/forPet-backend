package com.kusitms.forpet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "bookmark_place")
@NoArgsConstructor
@Getter
public class BookmarkPlace extends Bookmark{
    //장소 참조관계
    @ManyToOne
    @JoinColumn(name = "place_id")
    private placeInfo placeInfo;

    public void setUser(User user) {
        this.setUser(user);
        user.getBookmarkList().add(this);
    }

    //==연관관계 메서드==//
    public void setPlaceInfo(placeInfo placeInfo) {
        this.placeInfo = placeInfo;
        placeInfo.getBookMarkList().add(this);
    }

}
