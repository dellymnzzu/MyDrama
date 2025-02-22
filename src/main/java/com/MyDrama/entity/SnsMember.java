package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SnsMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String picture;


    private String role = "ROLE_USER";
    public SnsMember(String name, String email, String picture) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }



    public SnsMember update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }
}
