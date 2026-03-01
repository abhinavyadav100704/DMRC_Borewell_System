package com.dmrc.borewell.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Borewell")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Borewell {
    //    @Column(name = "authority_id")
//    private Integer authorityId;

//    @Column(name = "site_id", nullable = false)
//    private Integer siteId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borwell_id")
    private Integer borewellId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @Column(name = "borwell_no", nullable = false)
    private Integer borewellNo;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "distance_m")
    private Integer distanceM;

    @Column(name = "diameter")
    private Integer diameter;

    @Column(name = "depth", precision = 5, scale = 2)
    private BigDecimal depth;

    @Column(name = "location")
    private String location;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
}