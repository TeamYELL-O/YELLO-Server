package com.yello.server.domain.purchase.controller;

import static com.yello.server.global.common.SuccessCode.USER_PURCHASE_INFO_READ_SUCCESS;
import static com.yello.server.global.common.SuccessCode.USER_SUBSCRIBE_NEEDED_READ_SUCCESS;
import static com.yello.server.global.common.SuccessCode.VERIFY_RECEIPT_SUCCESS;

import com.yello.server.domain.purchase.dto.apple.AppleOrderResponse;
import com.yello.server.domain.purchase.dto.apple.AppleTransaction;
import com.yello.server.domain.purchase.dto.response.UserPurchaseInfoResponse;
import com.yello.server.domain.purchase.dto.response.UserSubscribeNeededResponse;
import com.yello.server.domain.purchase.service.PurchaseService;
import com.yello.server.domain.user.entity.User;
import com.yello.server.global.common.annotation.AccessTokenUser;
import com.yello.server.global.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "06. Purchase")
@RestController
@RequestMapping("api/v1/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Apple 구독 구매 검증 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppleOrderResponse.class))
        )
    })
    @PostMapping("/apple/verify/subscribe")
    public BaseResponse verifyAppleSubscriptionTransaction(
        @RequestBody AppleTransaction appleTransaction,
        @AccessTokenUser User user
    ) {
        purchaseService.verifyAppleSubscriptionTransaction(user.getId(), appleTransaction);

        return BaseResponse.success(VERIFY_RECEIPT_SUCCESS);
    }

    @Operation(summary = "Apple 열람권 구매 검증 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppleOrderResponse.class))
        )
    })
    @PostMapping("/apple/verify/ticket")
    public BaseResponse verifyAppleTicketTransaction(
        @RequestBody AppleTransaction appleTransaction,
        @AccessTokenUser User user
    ) {
        purchaseService.verifyAppleTicketTransaction(user.getId(), appleTransaction);

        return BaseResponse.success(VERIFY_RECEIPT_SUCCESS);
    }

    @Operation(summary = "구독 연장 유도 필요 여부 확인 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSubscribeNeededResponse.class))
        )
    })
    @GetMapping("/subscribeNeed")
    public BaseResponse<UserSubscribeNeededResponse> getUserSubscribeNeeded(
        @AccessTokenUser User user) {
        val data = purchaseService.getUserSubscribe(user, LocalDateTime.now());
        return BaseResponse.success(USER_SUBSCRIBE_NEEDED_READ_SUCCESS, data);
    }

    @Operation(summary = "구독 상태 및 구독권 갯수 조회 API", responses = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPurchaseInfoResponse.class))
        )
    })
    @GetMapping("/purchaseInfo")
    public BaseResponse<UserPurchaseInfoResponse> getUserPurchaseInfo(@AccessTokenUser User user) {
        val data = UserPurchaseInfoResponse.of(user);
        return BaseResponse.success(USER_PURCHASE_INFO_READ_SUCCESS, data);
    }

}
