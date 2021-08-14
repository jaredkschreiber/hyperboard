package com.assemblynext.hyperboard.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
@Entity
@Table(name="replies")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private BigInteger id;

    @Length(max=50)
    @NotBlank
    @Column(name="ipaddr_txt")
    private String ip;

    @Length(max=75)
    @Column(name="name_txt")
    private String name;

    @Length(max=100000)
    @NotBlank
    @Column(name="comment_txt")
    private String comment;

    @NotNull
    @Column(name="create_dt")
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name="fk_entry_id")
    private Entry entry;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "replyTo",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    private List<ReplyLink> repliesToThisPost;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "replyFrom",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    private List<ReplyLink> repliesFromThisPost;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reply",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    private List<Report> reports;
}
