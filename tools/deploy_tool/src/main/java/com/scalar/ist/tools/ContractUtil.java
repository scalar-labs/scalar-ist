package com.scalar.ist.tools;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.dl.client.config.ClientConfig;
import com.scalar.dl.client.service.ClientModule;
import com.scalar.dl.client.service.ClientService;
import com.scalar.dl.ledger.model.ContractExecutionResult;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import javax.json.JsonObject;

public class ContractUtil {

  private ClientService clientService;
  private String suffix = "";

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void setup(Properties properties) throws IOException {
    ClientConfig config = new ClientConfig(properties);
    Injector injector = Guice.createInjector(new ClientModule(config));
    clientService = injector.getInstance(ClientService.class);
  }

  public void registerCertificate() {
    try {
      clientService.registerCertificate();
    } catch (RuntimeException e) {
      //      System.out.println(e.getMessage());
    }
  }

  public void registerFunction(String id, String name, String path) {
    clientService.registerFunction(id, name, path);
  }

  public void registerContract(
      String id, String name, String path, Optional<JsonObject> properties) {
    JsonObject listContract = clientService.listContracts(id + suffix);
    if (listContract.size() == 0) {
      clientService.registerContract(id + suffix, name, path, properties);
    }
  }

  public ContractExecutionResult executeContract(
      String id, JsonObject contractArgument, Optional<JsonObject> functionArgument) {
    return clientService.executeContract(id + suffix, contractArgument, functionArgument);
  }

  private ContractExecutionResult printContractExecutionResult(ContractExecutionResult result) {
    if (result.getResult().isPresent()) {
      System.out.println("ContractExecutionResult:" + result.getResult().get());
    }
    return result;
  }
}
