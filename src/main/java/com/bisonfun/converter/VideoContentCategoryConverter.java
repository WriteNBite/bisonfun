package com.bisonfun.converter;

import com.bisonfun.model.enums.VideoContentCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class VideoContentCategoryConverter implements AttributeConverter<VideoContentCategory, String> {
    @Override
    public String convertToDatabaseColumn(VideoContentCategory videoContentCategory) {
        if(videoContentCategory == null){
            return null;
        }
        return videoContentCategory.getString();
    }

    @Override
    public VideoContentCategory convertToEntityAttribute(String s) {
        if(s == null){
            return null;
        }
        return Stream.of(VideoContentCategory.values())
                .filter(c -> c.getString().equals(s))
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
