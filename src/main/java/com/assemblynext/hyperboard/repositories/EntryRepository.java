package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.assemblynext.hyperboard.entities.Entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EntryRepository extends JpaRepository<Entry, BigInteger>{
    @Query("Select e from Entry e where e.pruneDate < CURRENT_TIMESTAMP and e.archive = FALSE")
    List<Entry> findPrunableEntries();

    @Query(value = "select e from Entry e inner join e.tags tags where tags.tag = ?1")
    Page<Entry> findByTagStr(String tagstr, Pageable pageable);

    Optional<Entry> findById(BigInteger id);

    @Query("select e from Entry e where e.createDate > ?1")
    List<Entry> latestEntries(LocalDateTime e1);

    @Query("select count(distinct(e.ip)) from Entry e where e.createDate > ?1")
    Integer uniqueEntryIps(LocalDateTime e1);

    @Query("select count(e) from Entry e where e.createDate > ?1")
    Integer uniqueEntries(LocalDateTime e1);
}
