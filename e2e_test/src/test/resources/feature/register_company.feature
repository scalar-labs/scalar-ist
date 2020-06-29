Feature: IST E2E

  Scenario: register company
    When when command file:build/resources/test/command/register_company.json
    # Then then command file:build/resources/test/command/register_company_check.json

#  Scenario: register company
#    Given prepare sysadmin contract
#    When execute_register_company
#    When execute_register_admin_user_profile
#    When execute_register_controller_user_profile
#    When execute_update_controller_user_profile
