package com.adhd.ad_hell.domain.board.command.application.service;

import com.adhd.ad_hell.domain.board.command.application.dto.response.BoardCommandResponse;
import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.FileType;
import com.adhd.ad_hell.domain.advertise.command.domain.repository.AdFileRepository;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardCreateRequest;
import com.adhd.ad_hell.domain.board.command.application.dto.request.BoardUpdateRequest;
import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import com.adhd.ad_hell.domain.board.command.domain.repository.BoardRepository;
import com.adhd.ad_hell.domain.category.command.domain.aggregate.Category;
import com.adhd.ad_hell.domain.category.command.domain.repository.CategoryRepository;
import com.adhd.ad_hell.domain.user.command.entity.User;
import com.adhd.ad_hell.domain.user.command.repository.UserCommandRepository;
import com.adhd.ad_hell.exception.BusinessException;
import com.adhd.ad_hell.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardCommandService {

    private final BoardRepository boardRepository;
    private final AdFileRepository adFileRepository;
    private final UserCommandRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorage fileStorage;

    // 게시글 등록
    public BoardCommandResponse create(BoardCreateRequest req, MultipartFile image) {
        //  FK 검증
        User writer = userRepository.findById(req.getWriterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        //  게시글 엔티티 생성 (Builder)
        Board board = Board.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .status(req.getStatus() != null ? req.getStatus() : "Y")
                .writer(writer)
                .category(category)
                .viewCount(0L)
                .build();

        boardRepository.save(board);

        //  이미지가 있을 경우 파일 저장
        if (image != null && !image.isEmpty()) {
            String savedName = fileStorage.store(image);

            AdFile fileMeta = AdFile.builder()
                    .fileTitle(image.getOriginalFilename())
                    .fileType(mapType(image.getContentType()))
                    .filePath(savedName)
                    .build();

            adFileRepository.save(fileMeta);
        }

        // 응답 DTO 반환
        return BoardCommandResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writerId(writer.getUserId())
                .content(board.getContent())
                .categoryId(category.getId())
                .status(board.getStatus())
                .viewCount(board.getViewCount())
                .build();
    }

    // 게시글 수정
    public BoardCommandResponse update(Long id, BoardUpdateRequest req, MultipartFile newImage) {

        // 1⃣ 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        //  카테고리 변경 (선택적)
        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        // 게시글 수정
        board.updateBoard(req.getTitle(), req.getContent(), category, req.getStatus());

        //  새 이미지 업로드
        if (newImage != null && !newImage.isEmpty()) {
            String savedName = fileStorage.store(newImage);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) fileStorage.deleteQuietly(savedName);
                }
            });

            AdFile fileMeta = AdFile.builder()
                    .fileTitle(newImage.getOriginalFilename() != null ? newImage.getOriginalFilename() : savedName)
                    .fileType(mapType(newImage.getContentType()))
                    .filePath(savedName)
                    .build();

            fileMeta.setBoard(board);
            adFileRepository.save(fileMeta);
        }

        // 응답 DTO 반환
        return BoardCommandResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writerId(board.getWriter().getUserId())
                .content(board.getContent())
                .categoryId(board.getCategory().getId())
                .status(board.getStatus())
                .viewCount(board.getViewCount())
                .build();
    }

    // 게시글 삭제
    public void delete(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 1️⃣ 커밋 후 물리 파일 삭제 예약
        adFileRepository.findByBoardId(id).forEach(file ->
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override public void afterCommit() {
                        fileStorage.deleteQuietly(file.getFilePath());
                    }
                })
        );

        //  DB 메타 삭제 + 게시글 삭제
        adFileRepository.deleteByBoardId(id);
        boardRepository.deleteById(id);
    }

    /** 파일 MIME → enum 변환 */
    private FileType mapType(String contentType) {
        if (contentType == null) return FileType.OTHER;
        if (contentType.startsWith("image/")) return FileType.IMAGE;
        if (contentType.startsWith("video/")) return FileType.VIDEO;
        if (contentType.startsWith("application/")) return FileType.DOCUMENT;
        return FileType.OTHER;
    }
}
