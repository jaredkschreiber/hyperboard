package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;

import com.assemblynext.hyperboard.entities.ReplyLink;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyLinkRepository extends JpaRepository<ReplyLink, BigInteger>{
}