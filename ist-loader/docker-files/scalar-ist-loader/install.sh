#!/bin/bash
check_response(){
if  [[ $1 == *"OK"* ]];
  then
    echo $2 has been registered
  elif [[ $1 == *"ALREADY_REGISTERED"* ]];
  then
    echo $2 has already been registered and is being skipped
  else
    echo $1 $2 registeration failed
    exit 1
  fi
}

register_cert(){
  echo registering certificate...
  OUTPUT=$(client/bin/register-cert $PROPERTIES)
  check_response "$OUTPUT" certificate
}

register_contracts(){
  echo registering contracts...
  OUTPUT=$(client/bin/register-contracts --contracts-file ./contracts.toml $PROPERTIES)
  check_response "$OUTPUT" contracts
}

register_functions(){
  echo registering functions...
  OUTPUT=$(client/bin/register-functions --functions-file ./functions.toml $PROPERTIES)
  check_response "$OUTPUT" functions
}

check_env(){
  echo checking if all env variables are setted ...
  if [[ -z "${LEDGER_HOST}" ]]; then
    $ENV_STATUS=1
    echo the required LEDGER_HOST variable is missing
  fi
  if [[ -z "${CLIENT_PROPERTIES_PATH}" ]]; then
    $ENV_STATUS=1
    echo the required CLIENT_PROPERTIES_PATH variable is missing
  fi
}
ENV_STATUS=0
check_env
if [[ $ENV_STATUS==0 ]]; then
  PROPERTIES="--properties $CLIENT_PROPERTIES_PATH"
  while ! nc -z $LEDGER_HOST 50051
  do
    echo waiting for ledger
    sleep 5
  done
  if [[ $IST_INSTALL_CONTRACTS == true ]];then
      register_cert
      register_contracts
  fi
  if [[ $IST_INSTALL_FUNCTIONS == true ]];then
      register_functions
  fi
fi
exit 0
