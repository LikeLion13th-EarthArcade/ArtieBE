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
    private Long id;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space;

    public void deleteImage() {
        isDeleted = true;
        markDeleted(); // 삭제 시간 정보 생성
    }

}
