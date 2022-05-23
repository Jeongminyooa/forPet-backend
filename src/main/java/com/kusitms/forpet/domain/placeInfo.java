package com.kusitms.forpet.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class placeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id",unique = true)
    private Long id;

    private String category;

    private String name;        //이름
    private String address;    //소재지 주소(도로명)

    private String longitude;     //경도
    private String latitude;    //위도

    private double starAvg;    //별점 평균
    private int reviewCnt;  //리뷰 수

    @JsonIgnore
    @OneToMany(mappedBy = "placeInfo")
    private List<Review> reviewList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "placeInfo")
    private List<BookmarkPlace> bookMarkList = new ArrayList<>();


    //== 비즈니스 로직 ==//
    public placeInfo setStarAvgAndReviewCnt(placeInfo placeInfo) {
        placeInfo.setReviewCnt(reviewList.size());

        int sum = 0;
        for(Review review : reviewList) {
            sum += review.getStar();
        }

        placeInfo.setStarAvg(Math.round((double)sum/reviewList.size()*10)/10.0);

        return placeInfo;
    }


}

