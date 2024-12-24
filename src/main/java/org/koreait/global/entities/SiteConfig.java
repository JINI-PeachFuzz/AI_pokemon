package org.koreait.global.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// DB에 넣는 데이터 아님
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteConfig {
    private String siteTitle;
    private String description;
    private String keyword;
    private int cssVersion;
    private int jsVersion;
    private boolean useEmailAuth;
}
