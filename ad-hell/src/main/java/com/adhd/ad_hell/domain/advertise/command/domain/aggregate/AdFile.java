package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import com.adhd.ad_hell.domain.board.command.domain.aggregate.Board;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ad_file")
public class AdFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")  // FK 컬럼명 유지
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Ad ad;

    /** 실제 저장된 파일명 (UUID 기반) */
    @Column(nullable = false, length = 255)
    private String storedName;

    /** 원본 파일명 (사용자가 업로드한 이름) */
    @Column(nullable = false, length = 255)
    private String originFileName;

    /** 파일 종류 (IMAGE / VIDEO 등) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FileType fileType;

    @Builder
    private AdFile(String storedName, String originFileName, FileType fileType) {
        this.storedName = storedName;
        this.originFileName = originFileName;
        this.fileType = fileType;
    }

    /** 정적 팩토리 메서드 */
    public static AdFile of(String storedName, String originFileName, FileType type) {
        return AdFile.builder()
                     .storedName(storedName)
                     .originFileName(originFileName)
                     .fileType(type)
                     .build();
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
