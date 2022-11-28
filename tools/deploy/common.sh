#!/bin/bash

# constants
REGISTER_CERTIFICATE_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-cert"
REGISTER_FUNCTIONS_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-functions"
REGISTER_CONTRACTS_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-contracts"
EXECUTE_CONTRACT_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/execute-contract"
LIST_CONTRACT_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/list-contracts"
TEST_STATUS=0

# funtions
run() {
  echo $@
  $@

  local return=$?
  if [ "$return" == '0' ]; then
    echo -e "\033[0;32mSUCCESSFUL\033[0m\n\n"
  else
    TEST_STATUS=1
    echo -e "\033[0;31mFAILED\033[0m\n\n"
  fi
  return $return
}

register_certificate() {
  local properties="$1"
  local status='1'
  local retry=0

  if [ "$properties" != '' ]; then
    while [ "$status" != '0' ] && [ $retry -lt 3 ]; do
      status_code=$($REGISTER_CERTIFICATE_BIN --config $properties | jq -r '.status_code')
      if [ ${status_code} = "OK" ] || [ ${status_code} = "CERTIFICATE_ALREADY_REGISTERED" ]; then
        status=0
      fi
      retry=$(expr $retry + 1)
    done
  fi

  return $status
}

register_functions() {
  local properties="$1"
  if [ "${properties}" == '' ]; then
    return '1'
  fi

  local functions_file="$2"
  if [ "${functions_file}" == '' ]; then
    return '1'
  fi

  $REGISTER_FUNCTIONS_BIN \
    --config ${properties} \
    --functions-file ${functions_file}

  return $?
}

register_contracts() {
  local properties="$1"
  if [ "${properties}" == '' ]; then
    return '1'
  fi

  local contracts_file="$2"
  if [ "${contracts_file}" == '' ]; then
    return '1'
  fi

  $REGISTER_CONTRACTS_BIN \
    --config ${properties} \
    --contracts-file ${contracts_file} \
    --ignore-registered

  return $?
}

execute_contract() {
  local properties="$1"
  if [ "${properties}" == '' ]; then
    return '1'
  fi

  local contract_id="$2"
  if [ "${contract_id}" == '' ]; then
    return '1'
  fi

  local contract_argument="$3"
  if [ "${contract_argument}" == '' ]; then
    return '1'
  fi

  $EXECUTE_CONTRACT_BIN \
    --config "${properties}" \
    --contract-id "${contract_id}" \
    --contract-argument "${!contract_argument}"

  return $?
}
