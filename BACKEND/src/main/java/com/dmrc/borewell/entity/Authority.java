package com.dmrc.borewell.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Authority")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Integer authorityId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "email", nullable = false)
    private String email;
}
