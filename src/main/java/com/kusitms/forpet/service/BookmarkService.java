package com.kusitms.forpet.service;

import com.kusitms.forpet.domain.BookmarkPlace;
import com.kusitms.forpet.domain.User;
import com.kusitms.forpet.domain.placeInfo;
import com.kusitms.forpet.repository.APIRep;
import com.kusitms.forpet.repository.BookmarkRep;
import com.kusitms.forpet.repository.UserRep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRep bookmarkRepository;
    private final APIRep apiRepository;
    private final UserRep userRepository;

    /**
     * 북마크 생성
     */
    @Transactional
    public Long createBookMark(Long placeid, Long userid) {
        placeInfo placeInfo = apiRepository.findById(placeid).get();
        User user = userRepository.findById(userid).get();

        //북마크 생성
        BookmarkPlace bookmark = new BookmarkPlace();
        bookmark.setPlaceInfo(placeInfo);
        bookmark.setUser(user);

        BookmarkPlace save = bookmarkRepository.save(bookmark);

        return save.getId();
    }
}
