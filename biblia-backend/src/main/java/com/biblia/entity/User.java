package com.biblia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.WhereJoinTable;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

@Getter
@Setter
@Entity
@ToString
@Table(name = "user", schema = "biblia")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    @Basic
    @Column(name = "login_id", nullable = false)
    private String loginId;
    @Basic
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "phone_number")
    private String phoneNumber;
    @Basic
    @Column(name = "role_code")
    private String roleCode;
    @Basic
    @Column(name = "birthday")
    private Date birthday;
    @Basic
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Basic
    @Column(name = "status")
    private Integer status;
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTime;
    @Basic
    @Column(name = "updated_time")
    private Timestamp updatedTime;
    @Basic
    @Column(name = "updated_user")
    private Integer updatedUser;
    @Basic
    @JsonIgnore
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    @ManyToMany(fetch = FetchType.EAGER)
    @WhereJoinTable(clause = "delete_flag = 1")
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;
}
