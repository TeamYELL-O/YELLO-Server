package com.yello.server.small.user;

import static com.yello.server.global.common.ErrorCode.AUTH_UUID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.USERID_NOT_FOUND_USER_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.YELLOID_NOT_FOUND_USER_EXCEPTION;

import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.exception.UserNotFoundException;
import com.yello.server.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;

public class FakeUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();
    private Long id = 0L;

    @Override
    public User save(User user) {
        if (user.getId() != null && user.getId() > id) {
            id = user.getId();
        }

        User newUser = User.builder()
            .id(user.getId() == null ? ++id : user.getId())
            .recommendCount(0L)
            .name(user.getName())
            .yelloId(user.getYelloId())
            .gender(user.getGender())
            .point(2000)
            .social(user.getSocial())
            .profileImage(user.getProfileImage())
            .uuid(user.getUuid())
            .deletedAt(null)
            .group(user.getGroup())
            .groupAdmissionYear(user.getGroupAdmissionYear())
            .email(user.getEmail())
            .build();

        data.add(newUser);
        return newUser;
    }

    @Override
    public User findById(Long id) {
        return data.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(USERID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public User findByUuid(String uuid) {
        return data.stream()
            .filter(user -> user.getUuid().equals(uuid))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(AUTH_UUID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public boolean existsByUuid(String uuid) {
        return data.stream()
            .anyMatch(user -> user.getUuid().equals(uuid));
    }

    @Override
    public User findByYelloId(String yelloId) {
        return data.stream()
            .filter(user -> user.getYelloId().equals(yelloId))
            .findFirst()
            .orElseThrow(() -> new UserNotFoundException(YELLOID_NOT_FOUND_USER_EXCEPTION));
    }

    @Override
    public List<User> findAllByGroupId(Long groupId) {
        return data.stream()
            .filter(user -> user.getGroup().getId().equals(groupId))
            .toList();
    }
}
