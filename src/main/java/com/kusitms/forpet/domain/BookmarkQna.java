package com.kusitms.forpet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bookmark_place")
@NoArgsConstructor
@Getter
public class BookmarkQna extends Bookmark{
    //백과사전 참조관계
    @ManyToOne
    @JoinColumn(name = "qna_id")
    private QnaBoard qnaBoard;

    //==연관관계 메서드==//
    public void setUser(User user) {
        this.setUser(user);
        user.getBookmarkQnaList().add(this);
    }

    public void setQnaBoard(QnaBoard qnaBoard) {
        this.qnaBoard = qnaBoard;
        qnaBoard.getBookmarkQnaList().add(this);
    }


}
