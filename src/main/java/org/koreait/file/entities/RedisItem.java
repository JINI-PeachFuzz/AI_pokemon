//package org.koreait.file.entities;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.redis.core.RedisHash;
//
//import java.io.Serializable;
//

//@Data
//@RedisHash(value = "test_hash", timeToLive = 300) // 6*5 5분정도만
//public class RedisItem implements Serializable {
//
//    // @Id 값은 무조건 유지 // 스프링꺼 사용해야함
//    @Id
//    private String key;
//
//    // 그아래 set 값 변경해도 상관 x
//
//    private int price;
//
//    private String productNm;
//}