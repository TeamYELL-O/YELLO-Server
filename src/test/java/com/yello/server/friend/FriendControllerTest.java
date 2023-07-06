package com.yello.server.friend;

import static org.assertj.core.api.Assertions.assertThat;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.entity.FriendRepository;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.group.entity.SchoolRepository;
import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.entity.UserRepository;
import com.yello.server.global.common.util.PaginationUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class FriendControllerTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    SchoolRepository schoolRepository;

    @Test
    @DisplayName("친구 생성에 성공합니다.")
    void addFriendTest() {
        //given
        School group = schoolRepository.save(new School("솝트", "서버", 32));
        User user = createNewUser(1L, "현정", "hj_p", Gender.FEMALE, group);
        User target = createNewUser(2L, "세훈", "훈세", Gender.MALE, group);

        //when
        Friend follow = friendRepository.save(Friend.createFriend(user, target));

        //then
        assertThat(target.getName()).isEqualTo(follow.getTarget().getName());
        assertThat(user.getName()).isEqualTo(follow.getUser().getName());
    }

    @Test
    @DisplayName("내 친구 전체 조회에 성공합니다.")
    void findAllMyFriendTest() {
        // given
        Pageable pageable = PaginationUtil.createPageable(0);
        School group = schoolRepository.save(new School("솝트", "서버", 32));
        User user = createNewUser(1L, "현정", "hj_p", Gender.FEMALE, group);
        User friendA = createNewUser(2L, "세훈", "훈세", Gender.MALE, group);
        User friendB = createNewUser(3L, "의제", "제의", Gender.MALE, group);

        // when
        friendRepository.save(Friend.createFriend(user, friendA));
        friendRepository.save(Friend.createFriend(user, friendB));

        List<Friend> friends = friendRepository.findAllFriendsByUserId(pageable, user.getId())
            .stream()
            .toList();

        // then
        assertThat(friends.size()).isEqualTo(2);
    }

    private User createNewUser(Long id, String name, String yelloId, Gender gender, School group) {
        return userRepository.save(
            new User(id, name, yelloId, gender, 0, Social.KAKAO, "profileImageUrl", "uuid", LocalDateTime.now(),
                group));
    }
}