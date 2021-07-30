package com.assemblynext.hyperboard.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import lombok.Data;

@Data
@Entity
@Table(name="reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private BigInteger id;

    @Length(max=50)
    @NotBlank
    @Column(name="ipaddr_txt")
    private String ip;

    @Length(max=250)
    @NotBlank
    @Column(name="reason_txt")
    private String reason;

    @ManyToOne
    @NotNull
    @JoinColumn(name="fk_entry_id")
    private Entry entry;

    @ManyToOne
    @JoinColumn(name="fk_reply_id")
    private Reply reply;
}