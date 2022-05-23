package com.kusitms.forpet.service;

import com.kusitms.forpet.domain.*;
import com.kusitms.forpet.dto.MyPageDto;
import com.kusitms.forpet.repository.CommentQnaRep;
import com.kusitms.forpet.repository.PetCardRep;
import com.kusitms.forpet.repository.QnaBoardRep;
import com.kusitms.forpet.repository.UserRep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final PetCardRep petCardRepository;
    private final UserRep userRepository;
    private final CommentQnaRep commentQnaRep;
    private final QnaBoardRep qnaBoardRep;
    private final S3Uploader s3Uploader;


    /**
     * 마이페이지 사용자 정보 조회
     */
    public MyPageDto.UserDetailDto getUser(Long userId) {
        User user = userRepository.findByUserId(userId);

        // 프로필 사진
        String profileImage;
        if(!StringUtils.isEmpty(user.getCustomImageUrl())) {
            profileImage = user.getCustomImageUrl();
        } else {
            profileImage = user.getImageUrl();
        }

        // 주소 인증 여부
        boolean isCertifiedAddress = false;
        if(!StringUtils.isEmpty(user.getAddress())) {
            isCertifiedAddress = true;
        }

        Optional<PetCard> petcard = petCardRepository.findByUser(user);
        // 동물 등록 카드 여부
        boolean isCertifiedPetCard = false;
        if(petcard.isPresent()) {
            isCertifiedPetCard = true;
        }

        MyPageDto.UserDetailDto userDetailDto = new MyPageDto.UserDetailDto(user.getUserId(), isCertifiedAddress, isCertifiedPetCard, user.getAddress().split("#"), user.getNickname(), user.getName(), profileImage);

        return userDetailDto;
    }
    /**
     * 마이페이지 사용자 정보 수정
     */
    public MyPageDto.UserDetailDto updateUser(Long userId, MyPageDto.UserUpdateDto dto, MultipartFile profileImage) {
        User user = userRepository.findByUserId(userId);

        // 프로필 사진
        if(!profileImage.isEmpty()) {
            // image file -> url
            String profileImageName = s3Uploader.uploadImage(profileImage);
            StringBuilder profileImageUrl = new StringBuilder();
            profileImageUrl.append("https://kusitms-forpet.s3.ap-northeast-2.amazonaws.com/");
            profileImageUrl.append(profileImageName);

            user.updateCustomImage(profileImageUrl.toString());
        }

        // 닉네임과 주소 업데이트
        if(StringUtils.isNotEmpty(dto.getNickname())) {
            user.updateNickname(dto.getNickname());
        }

        if(StringUtils.isNotEmpty(dto.getAddress())) {
            user.updateAddress(dto.getAddress());
        }

        userRepository.save(user);

        // 프로필 사진
        String profileImageDto;
        if(StringUtils.isNotEmpty(user.getCustomImageUrl())) {
            profileImageDto = user.getCustomImageUrl();
        } else {
            profileImageDto = user.getImageUrl();
        }

        // 주소 인증 여부
        boolean isCertifiedAddress = false;
        if(StringUtils.isNotEmpty(user.getAddress())) {
            isCertifiedAddress = true;
        }

        // 동물 등록 카드 여부
        Optional<PetCard> petcard = petCardRepository.findByUser(user);
        boolean isCertifiedPetCard = false;
        if(petcard.isPresent()) {
            isCertifiedPetCard = true;
        }

        MyPageDto.UserDetailDto userDetailDto = new MyPageDto.UserDetailDto(user.getUserId(), isCertifiedAddress, isCertifiedPetCard, user.getAddress().split("#"), user.getNickname(), user.getName(), profileImageDto);
        return userDetailDto;
    }
    /**
     * 마이페이지 히스토리 내가 쓴 글(커뮤니티, 백과사전)
     * @param userid
     */
    public Result getBoard(Long userid) {

        User user = userRepository.findById(userid).get();

        List<Community> CommunityList = user.getCommunityList();
        List<QnaBoard> QnaList = user.getQnaBoardList();

        //entity -> dto 변환 (커뮤니티)
        List<MyPageDto.HistoryBoardDTO> CommunityCollect = CommunityList.stream().map(m -> new MyPageDto.HistoryBoardDTO(m.getPostId(), "커뮤니티 - " + m.getCategory(),  m.getTitle()))
                .collect(Collectors.toList());

        //entity -> dto 변환 (퍼펫트 백과사전)
        List<MyPageDto.HistoryBoardDTO> QnaCollect = QnaList.stream().map(m -> new MyPageDto.HistoryBoardDTO(m.getId(), "퍼펫트 백과" , m.getTitle()))
                .collect(Collectors.toList());

        //return new Result(CommunityCollect, QnaCollect);
        return new Result(CommunityCollect, QnaCollect);
    }



    /**
     * 마이페이지 히스토리 답변한 글(커뮤니티, 백과사전)
     * 커뮤니티 댓글+대댓글 추가
     * @param userid
     */
    public List<MyPageDto.HistoryBoardDTO> getBoardByComment(Long userid) {

        List<Long> qnaIdList = commentQnaRep.find(userid);

        List<MyPageDto.HistoryBoardDTO> qnaCollect = new ArrayList<>();

        for(Long qnaId : qnaIdList) {
            QnaBoard qnaBoard = qnaBoardRep.findById(qnaId).get();
            qnaCollect.add(new MyPageDto.HistoryBoardDTO(qnaBoard.getId(), "퍼펫트 백과" ,
                    qnaBoard.getTitle()));
        }

        return qnaCollect;
    }



    /**
     * 마이페이지 북마크(오프라인 지도)
     * @param userid
     */
    public List<MyPageDto.BookmarkOfflineDto> getBookmarkOfflineMap(Long userid) {
        User user = userRepository.findById(userid).get();
        List<BookmarkPlace> list = user.getBookmarkList();

        //entity -> dto 변환
        List<MyPageDto.BookmarkOfflineDto> collect = list.stream().map(m -> new MyPageDto.BookmarkOfflineDto(m.getPlaceInfo().getId(), m.getPlaceInfo().getCategory(),
                m.getPlaceInfo().getName(), m.getPlaceInfo().getAddress(), m.getPlaceInfo().getStarAvg(), m.getPlaceInfo().getReviewCnt()))
                .collect(Collectors.toList());

        return collect;
    }


    /**
     * 마이페이지 북마크(커뮤니티, 백과사전)
     * @param userid
     */
    public Result getBookmarkBoard(Long userid) {

        User user = userRepository.findById(userid).get();

        List<BookmarkComm> bookmarkCommunityList = user.getBookmarkCommList();
        List<BookmarkQna> bookmarkQnaList = user.getBookmarkQnaList();

        //entity -> dto 변환 (커뮤니티)
        List<MyPageDto.HistoryBoardDTO> CommCollect = bookmarkCommunityList.stream().map(m -> new MyPageDto.HistoryBoardDTO(m.getCommunity().getPostId(),"커뮤니티 - " + m.getCommunity().getCategory(),
                        m.getCommunity().getTitle()))
                .collect(Collectors.toList());

        //entity -> dto 변환 (퍼펫트 백과사전)
        List<MyPageDto.HistoryBoardDTO> QnaCollect = bookmarkQnaList.stream().map(m -> new MyPageDto.HistoryBoardDTO(m.getQnaBoard().getId(), "퍼펫트 백과" ,
                        m.getQnaBoard().getTitle()))
                .collect(Collectors.toList());

        return new Result(CommCollect, QnaCollect);
    }

    @Data
    @AllArgsConstructor
    public static class Result<T> {
        private T community;
        private T qnaBoard;
    }
}
