package com.bisonfun.mapper;

import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.model.enums.VideoConsumingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper
public interface VideoConsumingStatusMapper {
    @ValueMappings({
            @ValueMapping(source = "PLANNING", target = "PLANNED"),
            @ValueMapping(source = "CURRENT", target = "WATCHING"),
            @ValueMapping(source = "COMPLETED", target = "COMPLETE")
    })
    VideoConsumingStatus fromMediaListStatus(MediaListStatus mediaListStatus);
}
