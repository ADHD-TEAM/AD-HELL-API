package com.adhd.ad_hell.domain.advertise.command.domain.aggregate;

import jakarta.persistence.Embeddable;
import lombok.Getter;

public enum AdStatus {
    ACTIVATE,
    DEACTIVATED;
}