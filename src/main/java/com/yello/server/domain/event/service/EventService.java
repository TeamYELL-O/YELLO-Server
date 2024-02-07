package com.yello.server.domain.event.service;

import static com.yello.server.global.common.ErrorCode.EVENT_COUNT_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_DATE_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.EVENT_TIME_BAD_REQUEST_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_CONFLICT_EXCEPTION;
import static com.yello.server.global.common.ErrorCode.IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION;
import static com.yello.server.global.common.util.ConstantUtil.GlobalZoneId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yello.server.domain.event.dto.request.EventJoinRequest;
import com.yello.server.domain.event.dto.response.EventResponse;
import com.yello.server.domain.event.dto.response.EventRewardResponse;
import com.yello.server.domain.event.entity.Event;
import com.yello.server.domain.event.entity.EventHistory;
import com.yello.server.domain.event.entity.EventInstance;
import com.yello.server.domain.event.entity.EventInstanceReward;
import com.yello.server.domain.event.entity.EventRewardMapping;
import com.yello.server.domain.event.entity.EventTime;
import com.yello.server.domain.event.entity.EventType;
import com.yello.server.domain.event.exception.EventBadRequestException;
import com.yello.server.domain.event.exception.EventNotFoundException;
import com.yello.server.domain.event.repository.EventRepository;
import com.yello.server.domain.user.entity.User;
import com.yello.server.domain.user.repository.UserRepository;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<EventResponse> getEvents(Long userId) throws JsonProcessingException {
        // exception
        final User user = userRepository.getById(userId);

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        List<EventResponse> result = new ArrayList<>();

        // 현재 날짜에 유효한 이벤트.
        final List<Event> eventList = eventRepository.findAll().stream()
            .filter(event -> now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))
            .toList();

        for (Event event : eventList) {
            // 현재 시각에 유효한 이벤트 시간대
            final List<EventTime> eventTimeList = eventRepository.findAllByEventId(event.getId()).stream()
                .filter(
                    eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(eventTime.getEndTime())
                )
                .toList();

            /**
             * EventTime을 바꾸면, EventInstance가 바뀌지 않음.
             * 이벤트 시간대가 바뀌면, 내일부터 적용된다고 말하기
             */
            // 이벤트 참가한 이력이 오늘이고, 현재 시각이 이벤트 시간에 유효하고, 남은 보상 카운트가 0인 이력
            final List<EventInstance> eventInstanceList = eventRepository.findInstanceAllByEventAndUser(event, user)
                .stream()
                .filter(eventInstance -> eventInstance.getInstanceDate().isAfter(event.getStartDate())
                    && eventInstance.getInstanceDate().isBefore(event.getEndDate())
                    && nowTime.isAfter(eventInstance.getStartTime()) && nowTime.isBefore(eventInstance.getEndTime())
                    && eventInstance.getRemainEventCount() == 0)
                .toList();

            // 해당 변수로 클라가 이벤트를 띄울지 판단하게 됨.
            // 이벤트 시간대가 있고, 보상 카운트가 0인 참여이력이 없을 때
            // k번 이벤트 참여는 보상 카운트로 구현한다.
            boolean isEventAvailable = !eventTimeList.isEmpty() && eventInstanceList.isEmpty();

            final EventTime eventTime = isEventAvailable ? eventTimeList.get(0) : null;
            final List<EventRewardMapping> eventRewardMappingList =
                isEventAvailable ? eventRepository.findAllByEventTimeId(eventTime.getId()) : null;

            result.add(EventResponse.of(event, eventTime, eventRewardMappingList));
        }

        return result;
    }

    @Transactional
    public void joinEvent(Long userId, UUID uuidIdempotencyKey, EventJoinRequest request) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<EventHistory> eventHistory = eventRepository.findHistoryByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isPresent()) {
            throw new EventBadRequestException(IDEMPOTENCY_KEY_CONFLICT_EXCEPTION);
        }

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();
        final Event event = eventRepository.getByTag(EventType.fromCode(request.tag()));

        if (!(now.isAfter(event.getStartDate()) && now.isBefore(event.getEndDate()))) {
            throw new EventBadRequestException(EVENT_DATE_BAD_REQUEST_EXCEPTION);
        }

        final List<EventTime> eventTimeList = eventRepository.findAllByEventId(event.getId()).stream()
            .filter(eventTime -> nowTime.isAfter(eventTime.getStartTime()) && nowTime.isBefore(eventTime.getEndTime()))
            .toList();
        if (eventTimeList.isEmpty()) {
            throw new EventBadRequestException(EVENT_TIME_BAD_REQUEST_EXCEPTION);
        }
        EventTime eventTime = eventTimeList.get(0);

        final EventHistory newEventHistory = eventRepository.save(EventHistory.builder()
            .idempotencyKey(uuidIdempotencyKey)
            .user(user)
            .build());

        eventRepository.save(EventInstance.builder()
            .eventHistory(newEventHistory)
            .event(event)
            .instanceDate(now)
            .startTime(eventTime.getStartTime())
            .endTime(eventTime.getEndTime())
            .remainEventCount(eventTime.getRewardCount())
            .build());
    }

    @Transactional
    public EventRewardResponse rewardEvent(Long userId, UUID uuidIdempotencyKey) {
        // exception
        final User user = userRepository.getById(userId);
        final Optional<EventHistory> eventHistory = eventRepository.findHistoryByIdempotencyKey(uuidIdempotencyKey);
        if (eventHistory.isEmpty()) {
            throw new EventNotFoundException(IDEMPOTENCY_KEY_NOT_FOUND_EXCEPTION);
        }
        final Optional<EventInstance> eventInstance = eventRepository.findInstanceByEventHistory(eventHistory.get());

        // logic
        ZonedDateTime now = ZonedDateTime.now(GlobalZoneId);
        OffsetTime nowTime = now.toOffsetDateTime().toOffsetTime();

        if (!now.toLocalDate().isEqual(eventInstance.get().getInstanceDate().toLocalDate())) {
            throw new EventBadRequestException(EVENT_DATE_BAD_REQUEST_EXCEPTION);
        }

        if (!(nowTime.isAfter(eventInstance.get().getStartTime()) && nowTime.isBefore(
            eventInstance.get().getEndTime()))) {
            throw new EventBadRequestException(EVENT_TIME_BAD_REQUEST_EXCEPTION);
        }

        if (eventInstance.get().getRemainEventCount() <= 0L) {
            throw new EventBadRequestException(EVENT_COUNT_BAD_REQUEST_EXCEPTION);
        }

        eventInstance.get().subRemainEventCount(1L);
        eventRepository.save(EventInstanceReward.builder()
            .eventInstance(eventInstance.get())
            .rewardTag("POINT")
            .rewardValue(200L)
            .rewardTitle("200 포인트를 얻었어요!")
            .rewardImage("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build());
        /**
         * TODO 랜덤값 구현
         */
        user.addPoint(200);
        return EventRewardResponse.builder()
            .rewardTag("POINT")
            .rewardValue(200L)
            .rewardTitle("200 포인트를 얻었어요!")
            .rewardImage("https://storage.googleapis.com/yelloworld/image/coin-stack.svg")
            .build();
    }
}
