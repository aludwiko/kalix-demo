package com.example.wallet.api;

import kalix.javasdk.action.Action;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static kalix.javasdk.StatusCode.ErrorCode.BAD_REQUEST;

@RequestMapping("/hello")
public class HelloWorld extends Action {

  @GetMapping("/{name}")
  public Effect<String> hi(@PathVariable String name) {
    if (name.length() <= 1) {
      return effects().error("Invalid name", BAD_REQUEST);
    } else {
      return effects().reply("Hi " + name);
    }
  }
}
