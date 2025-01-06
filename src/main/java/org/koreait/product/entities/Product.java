package org.koreait.product.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseMemberEntity;
import org.koreait.product.constants.DiscountType;

import java.util.List;

@Data
@Entity
public class Product extends BaseMemberEntity { // 책임전가로그확인으로 BaseMemberEntity 넣었음
    @Id @GeneratedValue
    private Long seq; // 상품관리할 수 있는 별도 코드가 따로 있음

    private boolean open; // 상품 노출 여부

    @Column(length = 45, nullable = false)
    private String gid; // 그룹 ID

    @Column(length = 150, nullable = false) // 한글의 경우 3바이트
    private String name; // 상품명


    private String summary; // 상품 요약 설명

    @Lob
    private String description; // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DiscountType discountType; // 할인 종류

    private double discount; // 할인율, 정가가격을 넣을 수 있음

    private int maxDiscount; // 최대 할인 금액

    private double pointRate; // 적립률 // 상품판매가 기준, 결제금액 기준 등등

    private int maxPoint; // 최대 적립금

    @Transient
    private List<FileInfo> mainImages; // 상품 상세 메인이미지

    @Transient
    private List<FileInfo> listImages; // 목록 이미지

    @Transient
    private List<FileInfo> editorImages; // 상세설명 이미지

    // Transient를 넣은건 후에 2차가공을 한 뒤에 넣는 것들이라고 생각하면 됨.

}
