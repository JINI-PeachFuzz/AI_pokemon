package org.koreait.file.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash(value = "test_hash", timeToLive = 300) // 6*5 5분정도만
public class RedisItem implements Serializable {

    @Id // 스프링꺼 사용해야함
    private String key;
    private int price;
    private String productNm;

}
