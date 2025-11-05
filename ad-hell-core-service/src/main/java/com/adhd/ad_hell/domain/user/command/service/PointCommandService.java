package com.adhd.ad_hell.domain.user.command.service;

import com.adhd.ad_hell.domain.user.command.dto.request.UserPointRequest;
import com.adhd.ad_hell.domain.user.command.dto.response.UserPointResponse;

public interface PointCommandService {

  UserPointResponse earnPoints(UserPointRequest userPointRequest);
}
