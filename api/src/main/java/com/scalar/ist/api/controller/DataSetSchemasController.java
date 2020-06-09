package com.scalar.ist.api.controller;

import com.scalar.ist.api.controller.dto.ReadAllDataSetSchemasRequestBody;
import com.scalar.ist.api.model.DataSetSchema;
import com.scalar.ist.api.security.Principal;
import com.scalar.ist.api.service.DataSetSchemasService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cmp/datasetschemas")
public class DataSetSchemasController {
  private final DataSetSchemasService dataSetSchemasService;

  @Autowired
  DataSetSchemasController(DataSetSchemasService dataSetSchemasService) {
    this.dataSetSchemasService = dataSetSchemasService;
  }

  @GetMapping
  public List<DataSetSchema> readAll(
      @RequestBody(required = false)
          ReadAllDataSetSchemasRequestBody readAllDataSetSchemasRequestBody,
      @AuthenticationPrincipal Principal principal) {
    return dataSetSchemasService.readAll(
        principal.getCompanyId(), readAllDataSetSchemasRequestBody);
  }

  @GetMapping("/{" + DataSetSchema.ORGANIZATION_ID + "}")
  public List<DataSetSchema> readAllWithOrganizationId(
      @PathVariable(DataSetSchema.ORGANIZATION_ID) String organizationId,
      @RequestBody(required = false) ReadAllDataSetSchemasRequestBody readAllPurposesRequestBody,
      @AuthenticationPrincipal Principal principal) {
    return dataSetSchemasService.readAllWithOrganizationId(
        principal.getCompanyId(), organizationId, readAllPurposesRequestBody);
  }

  @GetMapping("/{" + DataSetSchema.ORGANIZATION_ID + "}/{" + DataSetSchema.CREATED_AT + "}")
  public DataSetSchema read(
      @PathVariable(DataSetSchema.ORGANIZATION_ID) String organizationId,
      @PathVariable(DataSetSchema.CREATED_AT) long createdAt,
      @AuthenticationPrincipal Principal principal) {
    return dataSetSchemasService.read(principal.getCompanyId(), organizationId, createdAt);
  }
}
