package org.koreait.global.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.koreait.global.constants.Device;

@Data
// DB에 넣는 데이터 아님
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteConfig {
    private String siteTitle;
    private String description;
    private String keywords;
    private int cssVersion;
    private int jsVersion;
    private boolean useEmailAuth;
    private Device device;
}
