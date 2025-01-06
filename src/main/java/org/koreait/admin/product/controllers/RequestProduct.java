package org.koreait.admin.product.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.product.constants.DiscountType;


@Data
public class RequestProduct {

    private String mode;
    private Long seq; // 상품 번호, 수정시 필요

    @NotBlank
    private String gid; // 그룹아이디로 묶어서 하나의 상품그룹으로. / 상품정보수정시에도 사용예정

    @NotBlank
    private String name; // 상품명
    private String summary; // 상품 요약 설명
    private String description; // 상품 상세 설명

    private int consumerPrice; // 소비자가 / 할인되면 취소선이 추가되는 형태로 할 꺼
    private int salePrice; // 판매가 / 무료나눔의 경우 0원일 경우도 있음 배송비만 받는다던지..

    private DiscountType discountType; // 할인 종류
    private double discount; // 정가할인 금액(1000), 할인율(10.5%)
    private int maxDiscount; // 최대 할인 금액 // 쿠폰사용같은경우 얼마이상일때 10%할인인데 최대 얼마할인 이런거

    private double pointRate; // 적립률 - 결제 금액의 상품의 판매가 // 할인율이 적용된 금액
    private int maxPoint; // 최대 적립금


}
