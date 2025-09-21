package com.project.team5backend.domain.facility;


import com.project.team5backend.domain.facility.entity.Facility;
import com.project.team5backend.domain.facility.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacilityInitializer implements CommandLineRunner {

    private final FacilityRepository facilityRepository;

    @Override
    public void run(String... args) {
        if (facilityRepository.count() == 0) {
            facilityRepository.saveAll(List.of(
                    new Facility("RESTROOM", "화장실"),
                    new Facility("WIFI", "와이파이"),
                    new Facility("STROLLER_RENTAL", "유모차 대여")
            ));
        }
    }
}
