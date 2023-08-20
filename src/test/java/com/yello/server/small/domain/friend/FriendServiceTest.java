package com.yello.server.small.domain.friend;

import static com.yello.server.global.common.factory.PaginationFactory.createPageable;
import static com.yello.server.global.common.factory.PaginationFactory.createPageableLimitTen;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.yello.server.domain.friend.dto.request.KakaoRecommendRequest;
import com.yello.server.domain.friend.dto.response.FriendShuffleResponse;
import com.yello.server.domain.friend.dto.response.FriendsResponse;
import com.yello.server.domain.friend.dto.response.RecommendFriendResponse;
import com.yello.server.domain.friend.dto.response.SearchFriendResponse;
import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.friend.exception.FriendException;
import com.yello.server.domain.friend.exception.FriendNotFoundException;
import com.yello.server.domain.friend.repository.FriendRepository;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.domain.question.repository.QuestionRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import com.yello.server.domain.vote.repository.VoteRepository;
import com.yello.server.small.domain.question.FakeQuestionRepository;
import com.yello.server.small.domain.user.FakeUserRepository;
import com.yello.server.small.domain.vote.FakeVoteRepository;
import com.yello.server.util.TestDataUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class FriendServiceTest {


    private final UserRepository userRepository = new FakeUserRepository();
    private final FriendRepository friendRepository = new FakeFriendRepository();
    private final VoteRepository voteRepository = new FakeVoteRepository();
    private final QuestionRepository questionRepository = new FakeQuestionRepository();
    private final TestDataUtil testDataUtil = new TestDataUtil(
        userRepository,
        voteRepository,
        questionRepository,
        friendRepository
    );

    private FriendService friendService;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    @BeforeEach
    void init() {
        this.friendService = FriendService.builder()
            .userRepository(userRepository)
            .friendRepository(friendRepository)
            .voteRepository(voteRepository)
            .build();

        user1 = testDataUtil.generateUser(1L, 1L);
        user2 = testDataUtil.generateUser(2L, 2L);
        user3 = testDataUtil.generateUser(3L, 1L);
        user4 = testDataUtil.generateUser(4L, 1L);
        user5 = testDataUtil.generateUser(5L, 1L);

        testDataUtil.generateFriend(user1, user2);
        testDataUtil.generateFriend(user2, user1);
    }

    @Test
    void 친구_전체_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);

        // when
        final FriendsResponse friends = friendService.findAllFriends(pageable, userId);

        // then
        assertThat(friends.totalCount()).isEqualTo(1);
        assertThat(friends.friends().get(0).name()).isEqualTo("name2");
    }

    @Test
    void 친구_추가에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 3L;

        // when
        friendService.addFriend(userId, targetId);
        final Friend friend = friendRepository.getByUserAndTarget(userId, targetId);

        // then
        assertThat(friend.getUser().getName()).isEqualTo("name1");
        assertThat(friend.getTarget().getName()).isEqualTo("name3");
    }

    @Test
    void 친구_추가_시_존재하지_않는_유저_id인_경우에_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.addFriend(userId, targetId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_추가_시_이미_친구인_경우에_FriendException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 2L;

        // when
        // then
        assertThatThrownBy(() -> friendService.addFriend(userId, targetId))
            .isInstanceOf(FriendException.class)
            .hasMessageContaining("[FriendException] 이미 존재하는 친구입니다.");
    }

    @Test
    void 친구_셔플에_성공합니다() {
        // given
        final Long userId = 1L;

        // when
        friendRepository.save(Friend.createFriend(user1, user3));
        friendRepository.save(Friend.createFriend(user1, user4));
        friendRepository.save(Friend.createFriend(user1, user5));

        List<FriendShuffleResponse> firstShuffledList = friendService.findShuffledFriend(userId);
        List<FriendShuffleResponse> secoundShuffledList = friendService.findShuffledFriend(userId);

        // then
        assertThat(firstShuffledList.size()).isEqualTo(4);
        assertThat(secoundShuffledList.size()).isEqualTo(4);
    }

    @Test
    void 친구_셔플_시_존재하지_않는_유저_id인_경우에_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.findShuffledFriend(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_셔플_시_친구_수가_부족한_경우에_FriendException이_발생합니다() {
        // given
        final Long userId = 1L;

        // when
        // then
        assertThatThrownBy(() -> friendService.findShuffledFriend(userId))
            .isInstanceOf(FriendException.class)
            .hasMessageContaining("[FriendException] 친구가 4명 이하입니다.");
    }

    @Test
    void 학교_추천_친구_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);

        // when
        final RecommendFriendResponse recommendSchoolFriends =
            friendService.findAllRecommendSchoolFriends(
                pageable,
                userId
            );

        // then
        assertThat(recommendSchoolFriends.totalCount()).isEqualTo(3);
    }

    @Test
    void 친구_끊기에_성공합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 2L;

        // when
        friendService.deleteFriend(userId, targetId);
        final Optional<Friend> friends =
            friendRepository.findByUserAndTargetNotFiltered(userId, targetId);

        // then
        assertThat(friends.isEmpty()).isEqualTo(true);
    }

    @Test
    void 존재하지_않는_유저_친구_끊기_시_UserNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 999L;

        // when
        // then
        assertThatThrownBy(() -> friendService.deleteFriend(userId, targetId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("[UserNotFoundException] 탈퇴했거나 존재하지 않는 유저의 id 입니다.");
    }

    @Test
    void 친구_끊기_시_친구가_아니라면_FriendNotFoundException이_발생합니다() {
        // given
        final Long userId = 1L;
        final Long targetId = 4L;

        // when
        // then
        assertThatThrownBy(() -> friendService.deleteFriend(userId, targetId))
            .isInstanceOf(FriendNotFoundException.class)
            .hasMessageContaining("[FriendNotFoundException] 존재하지 않는 친구이거나 친구 관계가 아닙니다.");
    }

    @Test
    void 카카오_추천_친구_조회에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageable(page);
        final String[] friendKakaoId = {"1", "2", "3", "4"};
        final KakaoRecommendRequest request = KakaoRecommendRequest.builder()
            .friendKakaoId(friendKakaoId)
            .build();

        // when
        final RecommendFriendResponse allRecommendKakaoFriends =
            friendService.findAllRecommendKakaoFriends(
                pageable,
                userId,
                request
            );

        // then
        assertThat(allRecommendKakaoFriends.totalCount()).isEqualTo(3);
    }

    @Test
    void 친구_검색에_성공합니다() {
        // given
        final Long userId = 1L;
        final Integer page = 0;
        final Pageable pageable = createPageableLimitTen(page);
        final String keyword = "yello";

        // when
        SearchFriendResponse searchFriendResponse =
            friendService.searchFriend(userId, pageable, keyword);
        final int totalCount = searchFriendResponse.totalCount();

        // then
        assertThat(totalCount).isEqualTo(4);
        assertThat(searchFriendResponse.friendList().get(totalCount - 1).id()).isEqualTo(2);
    }
}