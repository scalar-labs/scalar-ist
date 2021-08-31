#!/bin/bash
PROPERTIES="--properties $CLIENT_PROPERTIES_PATH"

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

while ! nc -z $LEDGER_HOST 50051
do
  echo waiting for ledger
  sleep 5
done

register_cert
if [[ $IST_INSTALL_CONTRACTS == true ]];then
    register_contracts
fi
if [[ $IST_INSTALL_CONTRACTS == true ]];then
    register_functions
fi
exit 0