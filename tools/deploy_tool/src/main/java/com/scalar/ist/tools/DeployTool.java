package com.scalar.ist.tools;

import picocli.CommandLine;

import javax.json.Json;
import javax.json.JsonArray;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

public class DeployTool implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-f", "--file"},
      paramLabel = "Json File",
      description =
          "a file contains list of commands that will be executed. The file should be json.")
  private File commandsFile;

  public static void main(String... args) {
    CommandLine cmd = new CommandLine(new DeployTool());
    cmd.execute(args);
  }

  @Override
  public Integer call() {
    try {
      JsonArray array =
          Json.createReader(new BufferedInputStream(new FileInputStream(commandsFile))).readArray();
      Deploy deploy = new Deploy();
      deploy.process(array);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
