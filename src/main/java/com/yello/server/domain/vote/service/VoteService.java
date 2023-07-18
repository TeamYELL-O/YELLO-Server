package com.yello.server.domain.vote.service;

import com.yello.server.domain.cooldown.response.KeywordCheckResponse;
import com.yello.server.domain.cooldown.response.RevealNameResponse;
import com.yello.server.domain.cooldown.response.VoteCreateResponse;
import com.yello.server.domain.cooldown.response.VoteDetailResponse;
import com.yello.server.domain.cooldown.response.VoteFriendResponse;
import com.yello.server.domain.cooldown.response.VoteListResponse;
import com.yello.server.domain.question.dto.response.VoteAvailableResponse;
import com.yello.server.domain.question.dto.response.VoteQuestionResponse;
import com.yello.server.domain.vote.dto.request.CreateVoteRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface VoteService {

    VoteListResponse findAllVotes(Long userId, Pageable pageable);

    VoteDetailResponse findVoteById(Long id);

    List<VoteFriendResponse> findAllFriendVotes(Long userId, Pageable pageable);

    KeywordCheckResponse checkKeyword(Long userId, Long voteId);

    List<VoteQuestionResponse> findYelloVoteList(Long userId);

    VoteAvailableResponse checkVoteAvailable(Long userId);

    VoteCreateResponse createVote(Long userId, CreateVoteRequest request);

    RevealNameResponse revealNameHint(Long userId, Long voteId);

}
