package com.yello.server.util;

import com.yello.server.domain.friend.entity.Friend;
import com.yello.server.domain.group.entity.School;
import com.yello.server.domain.question.entity.Question;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.vote.entity.Vote;

public interface TestDataUtil {

    User generateUser(long index, long schoolIndex);

    User generateDeletedUser(long index, long schoolIndex);

    Friend generateFriend(User user, User target);

    Vote generateVote(long index, User sender, User receiver, Question question);

    Question generateQuestion(long index);

    School generateSchool(long index);
}

