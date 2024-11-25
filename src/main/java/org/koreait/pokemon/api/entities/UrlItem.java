package org.koreait.pokemon.api.entities;

import lombok.Data;

import java.util.List;

@Data
public class UrlItem {
    private String name;
    private String url;
    private List<UrlItem> results;
}
