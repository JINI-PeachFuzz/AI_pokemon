package org.koreait.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
// 공통 속성화한거
@Data
@MappedSuperclass // 공통 상위클래스임을 알려줌
@EntityListeners(AuditingEntityListener.class)//이벤트 리스터에 들어감 / 자동감지! 엔티티변화 , 값을 자동으로 넣어줌
public abstract class BaseMemberEntity extends BaseEntity {
    // 이메일 정보를 자동으로 넣어줄 꺼!
// BaseMemberEntity 다형성쿼리
    @CreatedBy
    @Column(length = 65, updatable = false)
    private String createdBy; // 파일을 처음 생성했을 떄 ID 저장되는 거

    @LastModifiedBy
    @Column(length = 65, insertable = false) // 수정할때만 들어가야해서
    private String modifiedBy; // 파일을 수정 했을 때 ID 저장되는 거
}
