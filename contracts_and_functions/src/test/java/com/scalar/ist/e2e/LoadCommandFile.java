package com.scalar.ist.e2e;

import com.scalar.ist.tools.Deploy;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadCommandFile {

  @When("^when command file:(.*)$")
  public void whenCommand(String commandsFile) {
    loadCommandFile(commandsFile);
  }

  @Then("^then command file:(.*)$")
  public void thenCommand(String commandsFile) {
    loadCommandFile(commandsFile);
  }

  public void loadCommandFile(String commandsFile) {
    System.out.println("commandsFile:[" + commandsFile + "]");

    try {
      JsonArray array =
          Json.createReader(new BufferedInputStream(new FileInputStream(commandsFile))).readArray();
      Deploy deploy = new Deploy();
      deploy.process(array);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
