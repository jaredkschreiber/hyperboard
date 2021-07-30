package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.assemblynext.hyperboard.entities.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
public interface TagRepository extends JpaRepository<Tag, BigInteger>{
    List<Tag> findByTag(String tag);

    @Query("select t from Tag t, Entry e where t.entry = e and e.createDate > ?1")
    List<Tag> latest(LocalDateTime v1);
}

