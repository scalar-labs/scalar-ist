#!/bin/bash

# constants
REGISTER_CERTIFICATE_BIN="$ROOTDIR/scalar/client/build/install/client/bin/register-cert"
REGISTER_FUNCTION_BIN="$ROOTDIR/scalar/client/build/install/client/bin/register-function"

# funtions
run() {
  echo $@
  $@

  local return=$?
  if [ "$return" == '0' ]; then
    echo -e "\033[0;32mSUCCESSFUL\033[0m\n\n"
  else
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
      $REGISTER_CERTIFICATE_BIN --config $properties
      status="$?"
      retry=$(expr $retry + 1)
    done
  fi

  return $status
}

register_function() {
  local properties="$1"
  if [ "$properties" == '' ]; then
    return '1'
  fi

  local function_id="$2"
  if [ "$function_id" == '' ]; then
    return '1'
  fi

  local function_binary_name="$3"
  if [ "$function_binary_name" == '' ]; then
    return '1'
  fi

  local function_file="$4"
  if [ "$function_file" == '' ]; then
    return '1'
  fi

  $REGISTER_FUNCTION_BIN \
    --config $properties \
    --function-id $function_id \
    --function-binary-name $function_binary_name \
    --function-class-file $function_file

  return $?
}
