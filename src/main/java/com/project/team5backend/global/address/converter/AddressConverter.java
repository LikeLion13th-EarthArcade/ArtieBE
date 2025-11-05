package com.project.team5backend.global.address.converter;

import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.domain.common.embedded.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressConverter {
    public static Address toAddress(AddressResDTO.AddressCreateResDTO dto) {
        return new Address(
                dto.city(),
                dto.district(),
                dto.neighborhood(),
                dto.roadNameAddress(),
                dto.detail(),
                dto.postalCode(),
                dto.latitude() != null ? BigDecimal.valueOf(dto.latitude()) : null,
                dto.longitude() != null ? BigDecimal.valueOf(dto.longitude()) : null
        );
    }

    public static AddressResDTO.AddressCreateResDTO toCreateAddressResDTO(String city, String district,String neighborhood, String roadName,
                                                                          String detail, String postalCode, Double latitude, Double longitude) {
        return AddressResDTO.AddressCreateResDTO.builder()
                .city(city)
                .district(district)
                .neighborhood(neighborhood)
                .roadNameAddress(roadName)
                .detail(detail)
                .postalCode(postalCode)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    public static Address toAddressCrawl(AddressResDTO.AddressCreateResDTO dto, String place) {
        return new Address(
                dto.city(),
                dto.district(),
                dto.neighborhood(),
                dto.roadNameAddress(),
                place,
                dto.postalCode(),
                dto.latitude() != null ? BigDecimal.valueOf(dto.latitude()) : null,
                dto.longitude() != null ? BigDecimal.valueOf(dto.longitude()) : null
        );
    }
}