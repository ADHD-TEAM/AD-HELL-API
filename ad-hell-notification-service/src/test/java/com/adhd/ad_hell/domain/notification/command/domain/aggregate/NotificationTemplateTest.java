package com.adhd.ad_hell.domain.notification.command.domain.aggregate;

import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.NotificationTemplateKind;
import com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums.YnType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTemplateTest {

    @DisplayName("update null 또는 blank는 무시하고 restore까지 동작한다")
    @Test
    void templateTest() {
        // given
        NotificationTemplate template = NotificationTemplate.builder()
                .templateKind(NotificationTemplateKind.NORMAL)
                .templateTitle("원래 제목")
                .templateBody("원래 본문")
                .deletedYn(YnType.no())
                .build();

        // when: null/blank 값으로 update -> 변화 없어야 함
        template.update(null, "   ", ""); // 모두 무시 대상
        assertEquals(NotificationTemplateKind.NORMAL, template.getTemplateKind());
        assertEquals("원래 제목", template.getTemplateTitle());
        assertEquals("원래 본문", template.getTemplateBody());

        // delete + restore
        template.delete();
        assertEquals(YnType.yes(), template.getDeletedYn());

        template.restore();
        assertEquals(YnType.no(), template.getDeletedYn());
    }
}
