package com.scalar.ist.api.controller;

import com.scalar.ist.api.controller.dto.ReadAllPurposesRequestBody;
import com.scalar.ist.api.model.Purpose;
import com.scalar.ist.api.security.Principal;
import com.scalar.ist.api.service.PurposesService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cmp/purposes")
public class PurposesController {
  private final PurposesService purposesService;

  @Autowired
  PurposesController(PurposesService purposesService) {
    this.purposesService = purposesService;
  }

  @GetMapping
  public List<Purpose> readAll(
      @RequestBody(required = false) ReadAllPurposesRequestBody readAllPurposesRequestBody,
      @AuthenticationPrincipal Principal principal) {
    return purposesService.readAll(principal.getCompanyId(), readAllPurposesRequestBody);
  }

  @GetMapping("/{" + Purpose.ORGANIZATION_ID + "}")
  public List<Purpose> readAllWithOrganizationId(
      @PathVariable(Purpose.ORGANIZATION_ID) String organizationId,
      @RequestBody(required = false) ReadAllPurposesRequestBody readAllPurposesRequestBody,
      @AuthenticationPrincipal Principal principal) {
    return purposesService.readAllWithOrganizationId(
        principal.getCompanyId(), organizationId, readAllPurposesRequestBody);
  }

  @GetMapping("/{" + Purpose.ORGANIZATION_ID + "}/{" + Purpose.CREATED_AT + "}")
  public Purpose read(
      @PathVariable(Purpose.ORGANIZATION_ID) String organizationId,
      @PathVariable(Purpose.CREATED_AT) long createdAt,
      @AuthenticationPrincipal Principal principal) {
    return purposesService.read(principal.getCompanyId(), organizationId, createdAt);
  }
}
