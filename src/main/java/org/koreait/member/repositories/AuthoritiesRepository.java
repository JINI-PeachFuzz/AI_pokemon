package org.koreait.member.repositories;

import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.AuthoritiesId;
import org.koreait.member.entities.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface AuthoritiesRepository extends JpaRepository<Authorities, AuthoritiesId>, QuerydslPredicateExecutor<Authorities> {

}
