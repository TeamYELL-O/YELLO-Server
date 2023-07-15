package com.yello.server.domain.authorization.dto.kakao;

import java.util.List;

public record KakaoFriendsInfo(
        List<KakaoFriend> elements,
        Integer total_count,
        Integer favorite_count,
        String before_url,
        String after_url,
        String result_id
) { }
