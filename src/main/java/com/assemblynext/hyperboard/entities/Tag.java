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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
@Table(name="tags",uniqueConstraints={@UniqueConstraint(columnNames={"fk_entry_id","tag_txt"})})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name="fk_entry_id")
    private Entry entry;

    @NotBlank
    @Column(name="tag_txt")
    private String tag;
}
