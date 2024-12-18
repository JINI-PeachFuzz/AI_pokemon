package org.koreait.pokemon.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.pokemon.controllers.PokemonSearch;
import org.koreait.pokemon.entities.Pokemon;
import org.koreait.pokemon.entities.QPokemon;
import org.koreait.pokemon.exceptions.PokemonNotFoundException;
import org.koreait.pokemon.repositories.PokemonRepository;
import org.koreait.wishlist.constants.WishType;
import org.koreait.wishlist.services.WishService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class PokemonInfoService {

    private final PokemonRepository pokemonRepository;
    private final HttpServletRequest request;
    private final Utils utils;
    private final JPAQueryFactory queryFactory;
    private final WishService wishService;

    /***
     * 포켓몬 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<Pokemon> getList(PokemonSearch search) { // PokemonSearch 커몬서치를 통해서 받게된거 // 글로벌 페이지 리스트데이터 참고
        int page = Math.max(search.getPage(), 1); // 페이지 번호 // 둘중에 큰수를 반환하므로 -1,0 이면 1이 나옴
        int limit = search.getLimit(); // 한페이지 당 레코드 갯수
        limit = limit < 1 ? 18 : limit;


        QPokemon pokemon = QPokemon.pokemon;


        /* 검색 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder(); // 검색처리가 1개일때는 q클래스로 넣으면 되는데 여러개일 때 (predicate를 통해) 검색조건 정한거
        String skey = search.getSkey();
        if (StringUtils.hasText(skey)) { // 키워드 검색
            andBuilder.and(pokemon.name
                    .concat(pokemon.nameEn)
                    .concat(pokemon.flavorText).contains(skey));
        }

        List<Long> seq = search.getSeq();
        if (seq != null && !seq.isEmpty()) {
            andBuilder.and(pokemon.seq.in(seq));
        } // 찜한 목록 조회하는 방법에 관한 것! // 마이페이지에 출력하는것만 추가하면 됨!

        /* 검색 처리 E */

        // pageable 도메인껄로 사용해야함
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(asc("seq"))); // 페이지는 0번 부터임

        Page<Pokemon> data = pokemonRepository.findAll(andBuilder, pageable);
        List<Pokemon> items = data.getContent(); // 조회된 목록

        // 추가 정보 처리
        items.forEach(this::addInfo); // 메서드 참조

        // Pagination(int page, int total, int ranges, int limit, HttpServletRequest request)

        int ranges = utils.isMobile() ? 5: 10;
        Pagination pagination = new Pagination(page, (int)data.getTotalElements(),ranges, limit, request);

        return new ListData<>(items, pagination); // 목록과 페이지에 있는걸 같이 보이게 하기 위해서!
    }

    // 내가 찜한 포켓몬 목록
    public ListData<Pokemon> getMyPokemons(PokemonSearch search) {
        List<Long> seq = wishService.getMyWish(WishType.POKEMON);
        if (seq == null || seq.isEmpty()) {
            return new ListData<>();
        }

        search.setSeq(seq);

        return getList(search);
    }

    /***
     * 포켓몬 단일 조회
     * @param seq
     * @return
     */
    public Pokemon get(Long seq) { // normal||fairy DB에서 이렇게 나오는게 있어서 2차가공이 필요함

        Pokemon item = pokemonRepository.findById(seq).orElseThrow(PokemonNotFoundException::new);

        // 추가 정보 처리
        addInfo(item, true); // 상세보기는 true가 들어감

        return item;

    }

    /***
     * 추가 정보 처리
     * @param item
     */
    private void addInfo(Pokemon item) {
        // abilities
        String abilities = item.getAbilities();
        if (StringUtils.hasText(abilities)) {
            item.set_abilities(Arrays.stream(abilities.split("\\|\\|")).toList());
        }

        // types
        String types = item.getTypes();
        if (StringUtils.hasText(types)) {
            item.set_types(Arrays.stream(types.split("\\|\\|")).toList());
        }


    }
    // 페이지 넘기는 거 관련
    private void addInfo(Pokemon item, boolean isView) { // boolean isView 은 확인밖에..
        addInfo(item);
        if (!isView) return;

        long seq = item.getSeq();
        long lastSeq = getLastSeq();

        // 이전 포켓몬 정보 - prevItem
        long prevSeq = seq - 1L; // 음수이면 마지막껄로 가는걸로 해줘야함
        prevSeq = prevSeq < 1L ? lastSeq : prevSeq; // 이전번호

        // 다음 포켓몬 정보 - prevItem
        long nextSeq = seq + 1L;
        nextSeq = nextSeq > lastSeq ? 1L : nextSeq;

        QPokemon pokemon = QPokemon.pokemon;
        List<Pokemon> items = (List<Pokemon>) pokemonRepository.findAll(pokemon.seq.in(prevSeq, nextSeq)); // 순서대로 가져와서 넣음

        Map<String, Object> prevItem = new HashMap<>();
        Map<String, Object> nextItem = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            Pokemon _item = items.get(i);

            Map<String, Object> data = _item.getSeq().longValue() == prevSeq ? prevItem : nextItem;
            data.put("seq", _item.getSeq());
            data.put("name", _item.getName());
            data.put("nameEn", _item.getNameEn());
        }

        item.setPrevItem(prevItem);
        item.setNextItem(nextItem);

    }

    private Long getLastSeq() { // 여기에 마지막꺼 가져오는거 넣을 꺼임
        QPokemon pokemon = QPokemon.pokemon;

        return queryFactory.select(pokemon.seq.max()) // select를 넣게 되면 튜플형도 있는데 이거는 영속성에 포함안되고 확인? 만 됨.
                .from(pokemon)
                .fetchFirst();
    }


}
