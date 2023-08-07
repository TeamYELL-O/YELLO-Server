package com.yello.server.domain.authorization.dto.request;

import com.yello.server.domain.user.entity.Gender;
import com.yello.server.domain.user.entity.Social;
import com.yello.server.domain.user.entity.Subscribe;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SignUpRequest(
    @NotNull Social social,
    @NotNull String uuid,
    @NotNull String deviceToken,
    @NotNull Subscribe subscribe,
    @NotNull @Email String email,
    @NotNull String profileImage,
    @NotNull Long groupId,
    @NotNull Integer groupAdmissionYear,
    @NotNull String name,
    @NotNull String yelloId,
    @NotNull Gender gender,
    @NotNull List<Long> friends,
    String recommendId
) {

}