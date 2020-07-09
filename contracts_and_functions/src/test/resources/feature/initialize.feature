Feature: IST E2E

  Scenario: Setup
    When when command file:build/resources/test/command/functions.json

  Scenario: Initialize
    When when command file:build/resources/test/command/initialize.json
