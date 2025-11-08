package com.adhd.ad_hell.domain.advertise.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.common.storage.FileStorageResult;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdCreateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.dto.request.AdUpdateRequest;
import com.adhd.ad_hell.domain.advertise.command.application.service.AdCommandService;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.Ad;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.FileType;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
class AdCommandServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private AdCommandService adCommandService;

    @Captor
    private ArgumentCaptor<Ad> adCaptor;

    @Test
    @DisplayName("광고 생성 성공 - 로그인 유저, 요청 DTO, 영상 파일 정상 주입")
    void createAd_withFiles_success() {
        // given
        LoginUserInfo loginUser = new LoginUserInfo(7L, "user@test.com", null);
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(SecurityUtil::getLoginUserInfo).thenReturn(loginUser);

            AdCreateRequest req = new AdCreateRequest(3L, "테스트 광고");

            MultipartFile file1 = mock(MultipartFile.class);
            MultipartFile file2 = mock(MultipartFile.class);
            given(file1.isEmpty()).willReturn(false);
            given(file2.isEmpty()).willReturn(false);
            given(file1.getOriginalFilename()).willReturn("video1.mp4");
            given(file2.getOriginalFilename()).willReturn("video2.mp4");

            FileStorageResult result1 = mock(FileStorageResult.class);
            FileStorageResult result2 = mock(FileStorageResult.class);
            given(result1.getStoredName()).willReturn("uuid-1.mp4");
            given(result2.getStoredName()).willReturn("uuid-2.mp4");

            given(fileStorage.store(file1)).willReturn(result1);
            given(fileStorage.store(file2)).willReturn(result2);

            // save는 캡처용으로만 사용(반환값 중요 X)
            given(adRepository.save(any(Ad.class))).willAnswer(invocation -> invocation.getArgument(0));

            // TransactionSynchronization 활성화 (단위 테스트에서만)
            TransactionSynchronizationManager.initSynchronization();
            try {
                // when
                adCommandService.createAd(req, List.of(file1, file2));
            } finally {
                // 정리
                TransactionSynchronizationManager.clearSynchronization();
            }

            // then
            verify(adRepository, times(1)).save(adCaptor.capture());
            Ad saved = adCaptor.getValue();

            assertThat(saved.getUserId()).isEqualTo(7L);
            assertThat(saved.getCategoryId()).isEqualTo(3L);
            assertThat(saved.getTitle()).isEqualTo("테스트 광고");
            assertThat(saved.getFiles()).hasSize(2);

            AdFile f1 = saved.getFiles().get(0);
            AdFile f2 = saved.getFiles().get(1);
            assertThat(f1.getStoredName()).isIn("uuid-1.mp4", "uuid-2.mp4");
            assertThat(f2.getStoredName()).isIn("uuid-1.mp4", "uuid-2.mp4");
            assertThat(f1.getOriginFileName()).isIn("video1.mp4", "video2.mp4");
            assertThat(f2.getOriginFileName()).isIn("video1.mp4", "video2.mp4");
            assertThat(f1.getFileType()).isEqualTo(FileType.VIDEO);
            assertThat(f2.getFileType()).isEqualTo(FileType.VIDEO);

            verify(fileStorage, times(1)).store(file1);
            verify(fileStorage, times(1)).store(file2);
        }
    }



    @Test
    @DisplayName("광고 수정 성공 - 제목/카테고리 변경")
    void updateAd_success() {
        // given
        Long adId = 11L;
        Ad existing = Ad.fromCreateDto(new AdCreateRequest(5L, "Old Title"), 9L);
        // 테스트 편의상 adId 세팅(리플렉션 또는 setter 사용)
        existing.setAdId(adId);

        given(adRepository.findById(adId)).willReturn(Optional.of(existing));

        AdUpdateRequest req = new AdUpdateRequest(8L, "New Title");

        // when
        adCommandService.updateAd(adId, req);

        // then
        assertThat(existing.getTitle()).isEqualTo("New Title");
        assertThat(existing.getCategoryId()).isEqualTo(8L);
        verify(adRepository, times(1)).findById(adId);
    }

    @Test
    @DisplayName("광고 수정 실패 - 광고 미존재 시 예외")
    void updateAd_notFound_throws() {
        // given
        Long adId = 404L;
        given(adRepository.findById(adId)).willReturn(Optional.empty());

        // when
        try {
            adCommandService.updateAd(adId, new AdUpdateRequest(1L, "T"));
        } catch (BusinessException e) {
            // then
            assertThat(e.getErrorCode()).isEqualTo(ErrorCode.AD_NOT_FOUND);
            verify(adRepository, times(1)).findById(adId);
            return;
        }
        throw new AssertionError("예외가 발생해야 합니다.");
    }

    @Test
    @DisplayName("광고 삭제 성공 - 파일 스토리지 삭제 호출 포함")
    void deleteAd_success() {
        // given
        Long adId = 22L;
        Ad ad = Ad.fromCreateDto(new AdCreateRequest(2L, "삭제 대상"), 100L);
        ad.setAdId(adId);

        // 파일 2개 부착
        ad.addFile(AdFile.of("s1.mp4", "o1.mp4", FileType.VIDEO));
        ad.addFile(AdFile.of("s2.mp4", "o2.mp4", FileType.VIDEO));

        given(adRepository.findById(adId)).willReturn(Optional.of(ad));

        // when
        adCommandService.deleteAd(adId);

        // then
        verify(adRepository, times(1)).deleteById(adId);
        verify(fileStorage, times(1)).deleteQuietly("s1.mp4");
        verify(fileStorage, times(1)).deleteQuietly("s2.mp4");
    }
}
