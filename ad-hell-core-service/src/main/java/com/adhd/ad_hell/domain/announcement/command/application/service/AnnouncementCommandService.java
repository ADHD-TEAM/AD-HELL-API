package com.adhd.ad_hell.domain.announcement.command.application.service;

import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.request.AnnouncementUpdateRequest;
import com.adhd.ad_hell.domain.announcement.command.application.dto.response.AnnouncementCommandResponse;
import com.adhd.ad_hell.domain.announcement.command.domain.aggregate.Announcement;
import com.adhd.ad_hell.domain.announcement.command.domain.repository.AnnouncementRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드 자동으로 생성자 주입해줌
@Transactional           // 메서드 전체가 하나의 트랜잭션으로 처리됨

// 공지사항 등록, 수정, 삭제
// ModelMapper 사용 안하고 만듬

public class AnnouncementCommandService {

    // DI(Dependency Injection)의존성 주입 영역
    // 객체가 사용할 의존 객체를 직접 생성하지 않고, 외부에서 주입 받는 것을 말함.
    // 클래스 내부에서 NEW로 직접 객체를 만들지 않고 스프링이 대신 만들어서 넣어줌.
    private final AnnouncementRepository announcementRepository; // 공지사항 저장소
    private final UserCommandRepository userRepository;          // 작성자(User) 조회용 저장소


    // 공지사항 등록
    public AnnouncementCommandResponse create(AnnouncementCreateRequest request) {

        // 작성자 정보 조회(writerId 기준)
        // 존재하지 않으면 USER_NOT_FOUND 예외 발생
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 상태(status)가 null이면 기본값 'Y'로 설정
        String status = (request.getStatus() != null) ? request.getStatus() : "Y";

        // Announcement 엔티티 생성 및 저장
        Announcement saved = announcementRepository.save(
                Announcement.builder()
                        .writer(writer)                 // 작성자(FK)
                        .title(request.getTitle())      // 제목
                        .content(request.getContent())  // 내용
                        .status(status)                 // 상태 (Y/N)
                        .build()
        );

        // 저장된 결과를 응답 DTO로 변환 후 반환
        return AnnouncementCommandResponse.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .status(saved.getStatus())
                .build();
    }

    // 공지사항 수정
    public AnnouncementCommandResponse update(Long id, AnnouncementUpdateRequest request) {

        // 수정 대상 공지사항 조회
        // 존재하지 않으면 ANNOUNCEMENT_NOT_FOUND 예외 발생
        Announcement ann = announcementRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 엔티티 내부의 update() 메서드 호출 -> 필요한 필드만 수정
        ann.update(request.getTitle(), request.getContent(), request.getStatus());

        // 수정된 엔티티 정보를 응답 DTO로 변환 후 반환
        return AnnouncementCommandResponse.builder()
                .id(ann.getId())
                .title(ann.getTitle())
                .content(ann.getContent())
                .status(ann.getStatus())
                .build();
    }

    // 공지사항 삭제
    public void delete(Long id) {

        // 존재하지 않으면 예외 발생
        if (!announcementRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }

        // DB에서 물리 삭제 수행
        announcementRepository.deleteById(id);
    }
}