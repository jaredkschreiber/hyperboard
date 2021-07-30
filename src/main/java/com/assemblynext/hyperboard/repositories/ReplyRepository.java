package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.assemblynext.hyperboard.entities.Reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReplyRepository extends JpaRepository<Reply, BigInteger>{
    @Query("select count(distinct(r.ip)) from Reply r where r.createDate > ?1")
    Integer uniqueReplyIps(LocalDateTime e1);

    @Query("select count(r) from Reply r where r.createDate > ?1")
    Integer uniqueReplies(LocalDateTime e1);
}