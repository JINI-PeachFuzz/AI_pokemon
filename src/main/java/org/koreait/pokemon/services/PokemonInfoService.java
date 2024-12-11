package org.koreait.pokemon.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.koreait.global.paging.ListData;
import org.koreait.pokemon.controllers.PokemonSearch;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.QPokemon;
import org.koreait.pokemon.exceptions.PokemonNotFoundException;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class PokemonInfoService {

    private final PokemonRepository pokemonRepository;

    /***
     * 포켓몬 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<Pokemon> getList(PokemonSearch search) {
        int page = Math.max(search.getPage(), 1); // 페이지 번호 // 둘중에 큰수를 반환하므로 -1,0 이면 1이 나옴
        int limit = search.getLimit(); // 한페이지 당 레코드 갯수
        limit = limit < 1 ? 20 : limit;


        QPokemon pokemon = QPokemon.pokemon;


        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();
        String skey = search.getSkey();
        if (StringUtils.hasText(skey)) { // 키워드 검색
            andBuilder.and(pokemon.name
                    .concat(pokemon.nameEn)
                    .concat(pokemon.flavorText).contains(skey));
        }
        /* 검색 처리 E */

        // pageable 도메인껄로 사용해야함
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("seq"))); // 페이지는 0번 부터임

        Page<Pokemon> data = pokemonRepository.findAll(andBuilder, pageable);
        List<Pokemon> items = data.getContent(); // 조회된 목록
        // 추가 정보 처리
        items.forEach(this::addInfo); // 메서드 참조


        return null;
    }

    /***
     * 포켓몬 단일 조회
     * @param seq
     * @return
     */
    public Pokemon get(Long seq) { // normal||fairy DB에서 이렇게 나오는게 있어서 2차가공이 필요함

        Pokemon item = pokemonRepository.findById(seq).orElseThrow(PokemonNotFoundException::new);

        // 추가 정보 처리
        addInfo(item);

        return item;

    }

    /***
     * 추가 정보 처리
     * @param item
     */
    private void addInfo(Pokemon item) {


    }

}
