package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.global.entity.BaseCreateDeleteEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceImage extends BaseCreateDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_image_id")
    private Long id;

    private String fileKey;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    public void deleteImage() {
        isDeleted = true;
        markDeleted(); // 삭제 시간 정보 생성
    }

}
