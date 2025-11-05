package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public enum FileType {
    VIDEO,
    IMAGE,
    DOCUMENT,
    OTHER
}
