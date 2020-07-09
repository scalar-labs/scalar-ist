Feature: IST E2E

  Scenario: upsert user profile admin
    When when command file:build/resources/test/command/upsert_user_profile_admin.json
    When when command file:build/resources/test/command/upsert_user_profile_controller.json
    When when command file:build/resources/test/command/upsert_user_profile_processor.json
