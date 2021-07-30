package com.assemblynext.hyperboard.entities;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

@Data
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames={"user_name"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private BigInteger id;

    @NotBlank
    @Column(name="user_name")
    @Length(max = 30)
    private String username;

    @NotBlank
    @Column(name="user_password")
    private String password;

    @Column(name="global_admin", insertable=false)
    private Boolean admin;

    @Column(name="global_mod", insertable=false)
    private Boolean mod;

    @Column(name="create_dt", insertable=false, updatable = false)
    private LocalDate createDate;
}