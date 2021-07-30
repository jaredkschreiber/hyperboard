package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.assemblynext.hyperboard.entities.Ban;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BanRepository extends JpaRepository<Ban, BigInteger>{
    List<Ban> findByIp(String ip);

    @Query("select b from Ban b where b.expDate < CURRENT_TIMESTAMP")
    List<Ban> pruneBans();
}