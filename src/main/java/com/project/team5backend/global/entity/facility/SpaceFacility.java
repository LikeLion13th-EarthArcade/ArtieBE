package com.project.team5backend.global.entity.facility;

import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.global.entity.BaseOnlyCreateTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SpaceFacility extends BaseOnlyCreateTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_facility_id")
    private Long id;

}
