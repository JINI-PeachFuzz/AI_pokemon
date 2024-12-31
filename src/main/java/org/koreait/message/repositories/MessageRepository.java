package org.koreait.message.repositories;

import org.koreait.message.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MessageRepository extends JpaRepository<Message, Long>, QuerydslPredicateExecutor<Message> {
    // 엔티티가 있으면 인터페이스도 있어야 디비에 반영이 가능해짐
}
