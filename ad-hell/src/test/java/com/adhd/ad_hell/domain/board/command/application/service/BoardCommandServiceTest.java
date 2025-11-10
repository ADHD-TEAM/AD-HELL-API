package com.adhd.ad_hell.domain.board.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import com.adhd.ad_hell.common.dto.LoginUserInfo;
import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.common.storage.FileStorageResult;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardCreateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardUpdateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.response.BoardCommandResponse;
import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.query.service.provider.UserProvider;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BoardCommandServiceTest {

    @InjectMocks
    private BoardCommandService boardCommandService;

    @Mock
    private UserProvider userProvider;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private FileStorage fileStorage;



    @Test
    @DisplayName("게시글 생성 성공 (이미지 파일 포함)")
    void createBoard_success_withImages() {
        // given
        long userId = 1L;
        long categoryId = 10L;
        BoardCreateRequest request = new BoardCreateRequest("제목", "내용", userId, categoryId, "Y", null);

        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "image1_content".getBytes())
        );

        // Mock 객체 설정
        LoginUserInfo mockLoginUser = new LoginUserInfo(userId, "testUser", null);
        User mockUser = User.builder().userId(userId).build();
        // Category의 생성자가 private이므로 mock 객체로 생성
        Category mockCategory = mock(Category.class);
        FileStorageResult mockFileResult = new FileStorageResult("stored1.jpg", "image1.jpg");

        // static 메소드 Mocking
        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class);
             MockedStatic<TransactionSynchronizationManager> mockedTxManager = Mockito.mockStatic(TransactionSynchronizationManager.class)) {

            // Mock 동작 정의
            mockedSecurityUtil.when(SecurityUtil::getLoginUserInfo).thenReturn(mockLoginUser);
            when(userProvider.getUserById(userId)).thenReturn(mockUser);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
            // toResponse()에서 category.getId()를 호출할 것이므로, getId()의 반환값을 지정해줘야 함
            when(mockCategory.getId()).thenReturn(categoryId);
            when(fileStorage.store(any(MultipartFile.class))).thenReturn(mockFileResult);
            mockedTxManager.when(() -> TransactionSynchronizationManager.registerSynchronization(any(TransactionSynchronization.class))).then(invocation -> null);

            ArgumentCaptor<Board> boardCaptor = ArgumentCaptor.forClass(Board.class);
            when(boardRepository.save(boardCaptor.capture())).then(invocation -> invocation.getArgument(0));

            // when
            BoardCommandResponse response = boardCommandService.createBoard(request, imageFiles);

            // then
            assertNotNull(response);
            assertEquals(categoryId, response.getCategoryId());

            Board capturedBoard = boardCaptor.getValue();
            assertEquals("제목", capturedBoard.getTitle());
            assertEquals(mockUser, capturedBoard.getWriter());
            assertEquals(mockCategory, capturedBoard.getCategory());
            assertEquals(1, capturedBoard.getFiles().size());
            assertEquals("stored1.jpg", capturedBoard.getFiles().get(0).getStoredName());

            verify(fileStorage, times(1)).store(any(MultipartFile.class));
            verify(boardRepository, times(1)).save(any(Board.class));
        }
    }


    @Test
    @DisplayName("게시글 수정 성공 테스트 (카테고리 포함)")
    void updateBoard_success_withCategory() {
        // given
        long boardId = 1L;
        long newCategoryId = 20L;

        BoardUpdateRequest request = BoardUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .status("N")
                .categoryId(newCategoryId)
                .build();

        // findById로 찾아올 Board 객체를 spy로 생성
        Board mockBoard = spy(Board.builder().id(boardId).build());
        // Category는 private 생성자이므로 mock 객체로 생성
        Category mockCategory = mock(Category.class);

        // Mock 동작 정의
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(mockBoard));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(mockCategory));

        // when
        boardCommandService.updateBoard(boardId, request);

        // then
        // 1. mockBoard의 updateBoard 메소드가 올바른 인자들로 호출되었는지 검증
        verify(mockBoard, times(1)).updateBoard(
                "수정된 제목",
                "수정된 내용",
                mockCategory,
                "N"
        );

        // 2. repository의 find 메소드들이 각각 1번씩 호출되었는지 검증
        verify(boardRepository, times(1)).findById(boardId);
        verify(categoryRepository, times(1)).findById(newCategoryId);
    }

    @Test
    @DisplayName("게시글 삭제 성공 테스트 (연관 파일 포함)")
    void deleteBoard_success_withFiles() {
        // given
        long boardId = 1L;

        // 삭제될 게시글에 포함된 파일 목록 생성
        AdFile file1 = AdFile.builder().storedName("stored1.jpg").build();
        AdFile file2 = AdFile.builder().storedName("stored2.png").build();

        // findById로 찾아올 Board 객체 생성
        Board mockBoard = Board.builder()
                .id(boardId)
                .files(List.of(file1, file2))
                .build();

        // boardRepository.findById가 mockBoard를 반환하도록 설정
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(mockBoard));

        // when
        boardCommandService.deleteBoard(boardId);

        // then
        // 1. boardRepository.deleteById가 올바른 ID로 호출되었는지 검증
        verify(boardRepository, times(1)).deleteById(boardId);

        // 2. fileStorage.deleteQuietly가 각 파일의 storedName으로 호출되었는지 검증
        verify(fileStorage, times(1)).deleteQuietly("stored1.jpg");
        verify(fileStorage, times(1)).deleteQuietly("stored2.png");
    }
    @Test
    @DisplayName("게시글 이미지 추가 성공 테스트")
    void appendImagesBoard_success() {
        // given
        long boardId = 1L;
        List<MultipartFile> imageFiles = List.of(
                new MockMultipartFile("image1", "image1.jpg", "image/jpeg", "content1".getBytes()),
                new MockMultipartFile("image2", "image2.png", "image/png", "content2".getBytes())
        );

        // findById로 찾아올 Board 객체를 spy로 생성
        Board mockBoard = spy(Board.builder().id(boardId).files(new ArrayList<>()).build());
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(mockBoard));

        // fileStorage.store가 호출될 때 순차적으로 다른 결과를 반환하도록 설정
        when(fileStorage.store(imageFiles.get(0))).thenReturn(new FileStorageResult("stored1.jpg", "url1"));
        when(fileStorage.store(imageFiles.get(1))).thenReturn(new FileStorageResult("stored2.png", "url2"));

        // static 메소드 Mocking
        try (MockedStatic<TransactionSynchronizationManager> mockedTxManager = Mockito.mockStatic(TransactionSynchronizationManager.class)) {
            mockedTxManager.when(() -> TransactionSynchronizationManager.registerSynchronization(any(TransactionSynchronization.class)))
                    .then(invocation -> null);

            // when
            int addedCount = boardCommandService.appendImagesBoard(boardId, imageFiles);

            // then
            // 1. 반환된 추가 개수 검증
            assertEquals(2, addedCount);

            // 2. board.addFile 메소드가 2번 호출되었는지 검증
            verify(mockBoard, times(2)).addFile(any(AdFile.class));

            // 3. fileStorage.store가 2번 호출되었는지 검증
            verify(fileStorage, times(2)).store(any(MultipartFile.class));

            // 4. 트랜잭션 동기화가 등록되었는지 검증
            mockedTxManager.verify(() -> TransactionSynchronizationManager.registerSynchronization(any(TransactionSynchronization.class)), times(1));
        }
    }

    @Nested
    @DisplayName("removeOneImageBoard 메서드는")
    class Describe_removeOneImageBoard {

        @Test
        @DisplayName("게시판과 저장된 파일 이름이 주어지면 파일을 삭제한다")
        void it_deletes_a_file_when_given_a_board_and_a_stored_file_name() {
            // given
            Long boardId = 1L;
            String storedName = "test.jpg";
            Board board = mock(Board.class);
            AdFile adFile = mock(AdFile.class);
            List<AdFile> files = new ArrayList<>();
            files.add(adFile);


            given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
            given(board.getFiles()).willReturn(files);
            given(adFile.getStoredName()).willReturn(storedName);

            // when
            boardCommandService.removeOneImageBoard(boardId, storedName);

            // then
            verify(fileStorage).deleteQuietly(storedName);
        }

        @Test
        @DisplayName("게시판을 찾을 수 없으면 BusinessException을 발생시킨다")
        void it_throws_a_business_exception_when_the_board_is_not_found() {
            // given
            Long boardId = 1L;
            String storedName = "test.jpg";
            given(boardRepository.findById(boardId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> boardCommandService.removeOneImageBoard(boardId, storedName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.BOARD_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("파일을 찾을 수 없으면 BusinessException을 발생시킨다")
        void it_throws_a_business_exception_when_the_file_is_not_found() {
            // given
            Long boardId = 1L;
            String storedName = "test.jpg";
            Board board = mock(Board.class);
            given(boardRepository.findById(boardId)).willReturn(Optional.of(board));
            given(board.getFiles()).willReturn(java.util.Collections.emptyList());

            // when / then
            assertThatThrownBy(() -> boardCommandService.removeOneImageBoard(boardId, storedName))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.FILE_NOT_FOUND.getMessage());
        }
    }
}
