package org.koreait.mypage.repositories;

import jakarta.servlet.http.HttpServletRequest;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.mypage.entities.Follow;
import org.koreait.mypage.entities.QFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

/**
 * 회원 Follow Repository
 *
 */
public interface FollowRepository extends JpaRepository<Follow, Long>, QuerydslPredicateExecutor<Follow> {

    Follow findByFollowingAndFollower(Member following, Member follower);

    // 해당 회원이 follow 하는 회원의 총합
    default long getTotalFollowings(Member member) {

        QFollow follow = QFollow.follow;

        return count(follow.following.eq(member));
    }

    // 해당 회원을 follow 하는 회원의 총합
    default long getTotalFollowers(Member member) {

        QFollow follow = QFollow.follow;

        return count(follow.follower.eq(member));
    }

    // 해당 회원이 follow 하는 회원 목록 페이지
    default ListData<Member> getFollowings(Member member, CommonSearch paging, HttpServletRequest request) {

        int page = Math.max(paging.getPage(), 1);

        int limit = paging.getLimit();

        limit = limit < 1 ? 20 : limit;

        QFollow follow = QFollow.follow;

        Pageable pageable = PageRequest.of(page -1, limit, Sort.by(desc("createdAt")));

        Page<Follow> data = findAll(follow.follower.eq(member), pageable);

        List<Follow> follows = data.getContent();

        List<Member> items = null;

        if (follows != null) {
            items = follows.stream().map(Follow::getFollowing).toList();
        }

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit);

        return new ListData<>(items, pagination);
    }


    // 해당 회원을 follow 하는 회원 목록 페이지
    default ListData<Member> getFollowers(Member member, CommonSearch paging, HttpServletRequest request) {

        int page = Math.max(paging.getPage(), 1);

        int limit = paging.getLimit();

        limit = limit < 1 ? 20 : limit;

        QFollow follow = QFollow.follow;

        Pageable pageable = PageRequest.of(page -1, limit, Sort.by(desc("createdAt")));

        Page<Follow> data = findAll(follow.following.eq(member), pageable);

        List<Follow> follows = data.getContent();

        List<Member> items = null;

        if (follows != null) {

            items = follows.stream().map(Follow::getFollower).toList();
        }

        Pagination pagination = new Pagination(page, (int)data.getTotalElements(), 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /*
    공통 부분 mode 처리 시도

    default ListData<Member> getFollows(int mode, Member member, CommonSearch paging, HttpServletRequest request) {

        int page = Math.max(paging.getPage(), 1);
        int limit = Math.max(paging.getPage(), 1);

        QFollow follow = QFollow.follow;

        QMember _mode = null;

        // 모드가 1일 경우 following 목록
        if (mode == 1) {

            _mode = follow.following;

            // 모드가 2일 경우 follower 목록
        } else if (mode ==2) {

            _mode = follow.follower;

        } else {

            throw new BadRequestException();
        }

        // 생성일자 역순 정렬
        Pageable pageable = PageRequest.of(page -1, limit, Sort.by(desc("createdAt")));

        Page<Follow> data = findAll(_mode.eq(member), pageable);
    }
     */

    // 회원이 follow 하는 회원 목록
    default List<Member> getFollowings(Member member) {

        QFollow follow = QFollow.follow;

        List<Follow> items = (List<Follow>)findAll(follow.follower.eq(member));

        if (items != null) {

            return items.stream().map(Follow::getFollowing).toList();
        }

        return null;
    }

    // 회원을 follow 하는 회원 목록
    default List<Member> getFollowers(Member member) {

        QFollow follow = QFollow.follow;

        List<Follow> items = (List<Follow>)findAll(follow.following.eq(member));

        if (items != null) {

            return items.stream().map(Follow::getFollower).toList();
        }

        return null;
    }
}