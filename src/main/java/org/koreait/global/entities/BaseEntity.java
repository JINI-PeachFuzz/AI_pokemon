package org.koreait.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
// 공통 속성화한거
@Getter @Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 사용할때마다 설정하게 새로고침느낌으로 MvcConfig에 추가함
public abstract class BaseEntity { // 이거는 다 필요한거

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 등록일시

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime modifiedAt; // 수정일시


    private LocalDateTime deletedAt; // 삭제일시
}
