package com.yello.server.domain.friend.controller;

import static com.yello.server.global.common.SuccessCode.ADD_FRIEND_SUCCESS;
import static com.yello.server.global.common.SuccessCode.READ_FRIEND_SUCCESS;
import static com.yello.server.global.common.util.PaginationUtil.createPageable;

import com.yello.server.domain.friend.dto.FriendsResponse;
import com.yello.server.domain.friend.service.FriendService;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "02. Friend")
@RestController
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 추가하기 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json")
        )
    })
    @PostMapping("/{targetId}")
    public ResponseEntity<BaseResponse> addFriend(
        @Parameter(name = "targetId", description = "친구 신청할 상대 유저의 아이디 값 입니다.")
        @Valid @PathVariable Long targetId,
        @RequestHeader("user-id") @Valid Long userId) {
        friendService.addFriend(userId, targetId);
        return ResponseEntity.ok(BaseResponse.success(ADD_FRIEND_SUCCESS));
    }
    
    @Operation(summary = "내 친구 전체 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FriendsResponse.class))
        )
    })
    @GetMapping
    public ResponseEntity<BaseResponse> findAllFriend(
        @Parameter(name = "page", description = "페이지네이션 페이지 번호입니다.", example = "1")
        @Valid @RequestParam Integer page,
        @RequestHeader("user-id") @Valid Long userId) {
        friendService.findAllFriends(createPageable(page), userId);
        return ResponseEntity.ok(BaseResponse.success(READ_FRIEND_SUCCESS));
    }

}
