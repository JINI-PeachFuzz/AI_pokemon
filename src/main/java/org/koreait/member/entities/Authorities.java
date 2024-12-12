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



}
