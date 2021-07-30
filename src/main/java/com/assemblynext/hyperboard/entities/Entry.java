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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
@Entity
@Table(name="entries")
public class Entry {
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

    @Length(max=75)
    @NotBlank
    @Column(name="subject_txt")
    private String subject;

    @Length(min=1400)
    @NotBlank
    @Column(name="comment_txt")
    private String comment;

    @NotNull
    @Column(name="create_dt")
    private LocalDateTime createDate;

    @NotNull
    @Column(name="prune_dt")
    private LocalDateTime pruneDate;

    @Length(max=5)
    @Column(name="attachment_type_txt")
    private String attachmentType;

    @NotNull
    @Column(name="content_warning")
    private Boolean contentWarning;

    @NotNull
    private Boolean archive;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entry",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    private List<Tag> tags;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entry",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    @OrderBy(value="id ASC")
    private List<Reply> replies;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "entry",cascade = {CascadeType.REMOVE},orphanRemoval=true)
    private List<Report> reports;
}
