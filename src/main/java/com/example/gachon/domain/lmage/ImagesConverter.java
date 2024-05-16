package com.example.gachon.domain.lmage;

import com.example.gachon.domain.lmage.dto.response.ImageResponseDto;

public class ImagesConverter {

    public static ImageResponseDto.ImageInfoDto toImageInfoDto(Images image) {
        return ImageResponseDto.ImageInfoDto.builder()
                .id(image.getId())
                .userId(image.getUser().getId())
                .name(image.getName())
                .type(image.getType())
                .url(image.getUrl())
                .build();
    }
}
