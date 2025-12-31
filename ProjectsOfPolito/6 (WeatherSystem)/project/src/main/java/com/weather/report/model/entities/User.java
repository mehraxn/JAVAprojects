package com.weather.report.model.entities;

import com.weather.report.model.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/// Represents a user in the weather report system
@Entity
@Table(name = "WR_USER")
public class User {

  @Id
  private String username;
  @Enumerated
  @Column(name = "user_type")
  private UserType type;

  User() { // for JPA compliance
  }

  public User(String username, UserType type) {
    this.username = username;
    this.type = type;
  }

  /// Retrieves the username of the user.
  public String getUsername() {
    return username;
  }

  /// Retrieves the type of the user.
  public UserType getType() {
    return type;
  }

}
