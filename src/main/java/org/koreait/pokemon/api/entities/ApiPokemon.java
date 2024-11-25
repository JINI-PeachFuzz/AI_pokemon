package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 알려진 항목이 없으면 무시 / 필요한 것만 가져와서 사용
public class ApiPokemon {
    private int id;
    private String name;
    private Sprites sprites;
    private int weight;
    private int height;
    private List<Types> types;
    private List<Ability> abilities;

    @JsonAlias("base_experience")
    private int baseExperience;

    private List<Names> names;

    @JsonAlias("flavor_text_entries")
    private List<FlavorText> flavorTextEntries;

}
