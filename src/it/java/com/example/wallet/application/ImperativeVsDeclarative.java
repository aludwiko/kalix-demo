package com.example.wallet.application;

import java.util.ArrayList;
import java.util.List;

record Person(String address) {
  public boolean hasValidData() {
    return false;
  }
}

public class ImperativeVsDeclarative {

  public List<String> getAddresses(List<Person> persons) {
    List<String> addresses = new ArrayList<>();
    for (int i = 0; i < persons.size(); i++) {
      Person person = persons.get(i);
      if (person.hasValidData()) {
        String address = person.address();
        addresses.add(address.trim());
      }
    }
    return addresses;
  }


  public List<String> getAddresses2(List<Person> persons) {
    return persons.stream()
      .filter(Person::hasValidData)
      .map(Person::address)
      .map(String::trim)
      .toList();
  }

}
