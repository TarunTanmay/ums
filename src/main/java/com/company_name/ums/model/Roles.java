package com.company_name.ums.model;

import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
