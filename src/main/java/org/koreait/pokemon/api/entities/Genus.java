package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genus {
    private String genus;
    private UrlItem language;
    // 한국어로 되어있는 내용만 업데이트 할려고 만듦
}
