package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.member.constants.Authority;

import java.io.Serializable;

@Data
@Entity
@IdClass(AuthoritiesId.class) // 복합키 사용
public class Authorities implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY) // 전체적으로 지연로딩을 하고 필요할때 즉시로딩
    private Member member;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Authority authority;


// id, id 두개인건 한명에게 여러권한을 줄 수있지만 중복된 권한은 안 주게 복합키로 정의했음, 다른사람은 동일 권한을 가질 수 는 있게해줌
}
