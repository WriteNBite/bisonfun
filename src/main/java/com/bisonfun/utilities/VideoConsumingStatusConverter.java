package com.bisonfun.utilities;

import com.bisonfun.dto.enums.VideoConsumingStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class VideoConsumingStatusConverter implements AttributeConverter<VideoConsumingStatus, String> {
    @Override
    public String convertToDatabaseColumn(VideoConsumingStatus videoConsumingStatus) {
        if(videoConsumingStatus == null){
            return null;
        }
        return videoConsumingStatus.getString();
    }

    @Override
    public VideoConsumingStatus convertToEntityAttribute(String s) {
        if(s == null){
            return null;
        }
        return Stream.of(VideoConsumingStatus.values())
                .filter(c -> c.getString().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
