package com.assemblynext.hyperboard.repositories;

import java.math.BigInteger;

import com.assemblynext.hyperboard.entities.Report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, BigInteger>{
}