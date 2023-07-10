package com.yello.server.domain.vote.controller;

import static com.yello.server.global.common.SuccessCode.READ_VOTE_SUCCESS;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

import com.yello.server.domain.vote.dto.response.VoteDetailResponse;
import com.yello.server.domain.vote.dto.response.VoteResponse;
import com.yello.server.domain.vote.service.VoteService;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "01. Vote")
@RestController
@RequestMapping("api/v1/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @Operation(summary = "내 투표 전체 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = VoteResponse.class)))),
    })
    @GetMapping
    public BaseResponse<List<VoteResponse>> findAllMyVotes(
        @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
        @RequestParam Integer page) {
        val data = voteService.findAllVotes(createPageable(page));
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }

    @Operation(summary = "투표 상세 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = VoteDetailResponse.class))),
    })
    @GetMapping("/{voteId}")
    public BaseResponse<VoteDetailResponse> findVote(@PathVariable Long voteId) {
        val data = voteService.findVoteById(voteId);
        return BaseResponse.success(READ_VOTE_SUCCESS, data);
    }
}
