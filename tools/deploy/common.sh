#!/bin/bash

# constants
REGISTER_CERTIFICATE_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-cert"
REGISTER_FUNCTIONS_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-functions"
REGISTER_CONTRACT_BIN="$ROOTDIR/scalardl-java-client-sdk/bin/register-contract"
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

register_contract() {
  local properties="$1"
  if [ "${properties}" == '' ]; then
    return '1'
  fi

  local contract_id="$2"
  if [ "${contract_id}" == '' ]; then
    return '1'
  fi

  local contract_binary_name="$3"
  if [ "${contract_binary_name}" == '' ]; then
    return '1'
  fi

  local contract_file="$4"
  if [ "${contract_file}" == '' ]; then
    return '1'
  fi

  local contract_properties="$5"

  is_available=$($LIST_CONTRACT_BIN --config "${properties}" \
    --contract-id "${contract_id}" | jq '.output | length')

  if [ $is_available == 0 ]; then
    if [ "$contract_properties" == '' ]; then
      $REGISTER_CONTRACT_BIN \
        --properties "${properties}" \
        --contract-binary-name "${contract_binary_name}" \
        --contract-id "${contract_id}" \
        --contract-class-file "${contract_file}"
    else
      $REGISTER_CONTRACT_BIN \
        --properties "${properties}" \
        --contract-binary-name "${contract_binary_name}" \
        --contract-id "${contract_id}" \
        --contract-class-file "${contract_file}" \
        --contract-properties "${!contract_properties}"
    fi
  fi

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
