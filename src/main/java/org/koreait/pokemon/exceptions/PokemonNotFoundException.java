package org.koreait.pokemon.exceptions;


import org.koreait.global.exceptions.scripts.AlertBackException;
import org.springframework.http.HttpStatus;


public class PokemonNotFoundException extends AlertBackException {
    public PokemonNotFoundException() {
        super("NotFound.pokemon", HttpStatus.NOT_FOUND);
        setErrorCode(true); // 에러코드를 찾아서 출력할거임
    }



}
