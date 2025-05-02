package com.codigo.code.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int credit;

    private String description;

    private int expiredDateCount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code", referencedColumnName = "countryCode")
    private Country country;

    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPackage> userPackages = new ArrayList<>();
}
