package social;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
class Person {
  @Id
  private String code;
  private String name;
  private String surname;

  Person() {
    // default constructor is needed by JPA
  }

  Person(String code, String name, String surname) {
    this.code = code;
    this.name = name;
    this.surname = surname;
  }

  String getCode() {
    return code;
  }

  String getName() {
    return name;
  }

  String getSurname() {
    return surname;
  }

  //....
}
