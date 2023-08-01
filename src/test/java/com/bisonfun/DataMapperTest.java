package com.bisonfun;

import com.bisonfun.mapper.VideoConsumingStatusMapper;
import com.bisonfun.model.enums.MediaListStatus;
import com.bisonfun.model.enums.VideoConsumingStatus;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DataMapperTest {
    @Autowired
    private VideoConsumingStatusMapper videoConsumingStatusMapper;

    @Test
    public void fromMediaListStatusTest(){
        Set<VideoConsumingStatus> statuses = new HashSet<>();
        for(MediaListStatus status : MediaListStatus.values()){
            statuses.add(videoConsumingStatusMapper.fromMediaListStatus(status));
        }
        for(VideoConsumingStatus status : statuses){
            assertTrue(Arrays.stream(VideoConsumingStatus.values()).collect(Collectors.toList()).contains(status));
        }
    }
}
