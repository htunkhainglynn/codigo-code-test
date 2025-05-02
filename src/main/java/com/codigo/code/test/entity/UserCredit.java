package com.codigo.code.test.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int remainingCredits;
    private int expiredDateCount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_code", referencedColumnName = "countryCode")
    private Country country;

    @ManyToOne(optional = false)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @ToString.Exclude
    @JsonIgnore
    private User user;

}

