package com.assemblynext.hyperboard.entities;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="replylinks")
public class ReplyLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name="fk_reply_id")
    private Reply replyTo;

    @ManyToOne
    @JoinColumn(name="fk_reply_id2")
    private Reply replyFrom;
}
