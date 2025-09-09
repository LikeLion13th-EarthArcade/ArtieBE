package com.project.team5backend.domain.space.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/spaces")
@Tag(name = "SPACE RESERVATION", description = "공간 예약 관련 API")
public class ReservationController {
}


