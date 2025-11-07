package com.adhd.ad_hell.domain.board.command.application.service;

import com.adhd.ad_hell.common.storage.FileStorage;
import com.adhd.ad_hell.common.storage.FileStorageResult;
import com.adhd.ad_hell.common.util.SecurityUtil;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.AdFile;
import com.adhd.ad_hell.domain.advertise.command.domain.aggregate.FileType;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final CategoryRepository categoryRepository;
    private final FileStorage fileStorage;
    private final SecurityUtil securityUtil;
    private final UserProvider userProvider;

    /* ===================== Create ===================== */

    /** 게시글 등록(다중 이미지) */
    public BoardCommandResponse createBoard(BoardCreateRequest req, List<MultipartFile> imageFiles) {
        Long userId = securityUtil.getLoginUserInfo().getUserId();
        User writer = userProvider.getUserById(userId);

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Board board = Board.create(writer, category, req.getTitle(), req.getContent(), req.getStatus());
        boardRepository.save(board);

        List<String> storedNames = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) continue;

                // 로컬 저장
                FileStorageResult result = fileStorage.store(file);

                // 보상 삭제용 저장 이름 수집
                storedNames.add(result.getStoredName());

                // DB 저장: storedName + originFileName 둘 다 저장, 타입은 IMAGE
                AdFile adFile = AdFile.of(
                        result.getStoredName(),
                        file.getOriginalFilename(),
                        FileType.IMAGE
                );
                board.addFile(adFile);
            }
        }

        // 트랜잭션 실패 시 물리 파일 보상 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    storedNames.forEach(fileStorage::deleteQuietly);
                }
            }
        });

        return toResponse(board);
    }

    /** 게시글 등록(단일 이미지) — 컨트롤러 호환용 */
    public BoardCommandResponse createBoard(BoardCreateRequest req, MultipartFile image) {
        return createBoard(req, image == null ? List.of() : List.of(image));
    }

    /* ===================== Update ===================== */

    /** 게시글 수정(파일 제외) */
    public void updateBoard(Long boardId, BoardUpdateRequest req) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        board.updateBoard(req.getTitle(), req.getContent(), category, req.getStatus());
        // JPA 더티체킹으로 반영
    }

    /* ===================== Delete ===================== */

    /** 게시글 삭제(연관 파일 엔티티 + 물리 파일) */
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 물리 파일명 먼저 수집
        List<String> storedNames = board.getFiles().stream()
                .map(AdFile::getStoredName)
                .toList();

        // 엔티티 삭제 (자식 파일 레코드도 orphanRemoval=true로 제거)
        boardRepository.deleteById(boardId);

        // 물리 파일 정리(실패 무시)
        storedNames.forEach(fileStorage::deleteQuietly);
    }

    /* ===================== Files ===================== */

    /** 기존 게시글에 이미지 추가 업로드 */
    public int appendImagesBoard(Long boardId, List<MultipartFile> imageFiles) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        List<String> storedNames = new ArrayList<>();
        int added = 0;

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) continue;

                FileStorageResult result = fileStorage.store(file);
                storedNames.add(result.getStoredName());

                AdFile adFile = AdFile.of(
                        result.getStoredName(),
                        file.getOriginalFilename(),
                        FileType.IMAGE
                );
                board.addFile(adFile);
                added++;
            }
        }

        // 롤백 보상
        if (!storedNames.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) {
                        storedNames.forEach(fileStorage::deleteQuietly);
                    }
                }
            });
        }

        return added;
    }

    /** storedName 기준 이미지 1건 삭제 */
    public void removeOneImageBoard(Long boardId, String storedName) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        boolean removed = removeFileEntity(board, storedName);
        if (!removed) throw new BusinessException(ErrorCode.FILE_NOT_FOUND);

        fileStorage.deleteQuietly(storedName);
    }

    /* ==================== Helpers ==================== */

    private boolean removeFileEntity(Board board, String storedName) {
        for (Iterator<AdFile> it = board.getFiles().iterator(); it.hasNext();) {
            AdFile f = it.next();
            if (storedName.equals(f.getStoredName())) {
                it.remove();
                f.setBoard(null); // orphanRemoval → DB 삭제
                return true;
            }
        }
        return false;
    }

    private BoardCommandResponse toResponse(Board board) {
        return BoardCommandResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writerId(board.getWriter().getUserId()) // ✅ getUserId 사용
                .content(board.getContent())
                .categoryId(board.getCategory().getId())
                .status(board.getStatus())
                .viewCount(board.getViewCount())
                .build();
    }
}
