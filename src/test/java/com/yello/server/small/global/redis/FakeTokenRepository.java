package com.yello.server.small.global.redis;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import com.yello.server.global.common.ErrorCode;
import com.yello.server.infrastructure.redis.exception.RedisNotFoundException;
import com.yello.server.infrastructure.redis.repository.TokenRepository;
import java.util.HashMap;

public class FakeTokenRepository implements TokenRepository {

    private final HashMap<Long, ServiceTokenVO> data = new HashMap<>();
    private final HashMap<String, String> deviceTokenData = new HashMap<>();

    @Override
    public void set(Long key, ServiceTokenVO value) {
        data.put(key, value);
    }

    @Override
    public void setDeviceToken(String uuid, String deviceToken) {
        deviceTokenData.put(uuid, deviceToken);
    }

    @Override
    public ServiceTokenVO get(Long key) {
        return data.get(Long.parseLong(key.toString()));
    }

    @Override
    public String getDeviceToken(String uuid) {
        if (!hasKey(uuid)) {
            throw new RedisNotFoundException(ErrorCode.REDIS_NOT_FOUND_UUID);
        }
        return deviceTokenData.get(uuid);
    }

    @Override
    public void deleteDeviceToken(String uuid) {
        // do nothing
    }

    @Override
    public Boolean hasKey(String uuid) {
        return deviceTokenData.containsKey(uuid);
    }
}
