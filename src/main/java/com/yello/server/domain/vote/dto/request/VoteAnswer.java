package com.yello.server.domain.vote.dto.request;

public record VoteAnswer(
    Long friendId,
    Long questionId,
    String keywordName,
    Integer colorIndex
) {

}
