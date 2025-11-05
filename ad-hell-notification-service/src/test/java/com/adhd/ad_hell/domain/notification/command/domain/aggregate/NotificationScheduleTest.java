package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationScheduleStatus;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationScheduleTest {

    @DisplayName("상태변경 메서드들이 정상동작한다")
    @Test
    void scheduleTest() {
        // given
        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("제목")
                .templateBody("본문")
                .deletedYn(YnType.no())
                .build();

        LocalDateTime now = LocalDateTime.now();

        NotificationSchedule schedule = NotificationSchedule.builder()
                .template(template)
                .scheduleStatus(NotificationScheduleStatus.SCHEDULED)
                .scheduledAt(now.plusHours(1))
                .sentAt(null)
                .deletedYn(YnType.no())
                .build();

        // when & then - 완료 처리
        LocalDateTime sentAt = now.plusHours(2);
        schedule.markCompleted(sentAt);
        assertEquals(NotificationScheduleStatus.COMPLETED, schedule.getScheduleStatus());
        assertEquals(sentAt, schedule.getSentAt());

        // 실패 처리
        schedule.markFailed();
        assertEquals(NotificationScheduleStatus.FAILED, schedule.getScheduleStatus());

        // 취소 처리
        schedule.cancel();
        assertEquals(NotificationScheduleStatus.CANCELLED, schedule.getScheduleStatus());

        // 삭제 처리
        schedule.delete();
        assertEquals(YnType.yes(), schedule.getDeletedYn());
    }
}
