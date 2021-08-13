STATUS=0
register_cert(){
  echo registering certificate...
  while IFS= read line;
    do
      OUTPUT=$($line --properties $CLIENT_PROPERTIES_PATH | sed -n '2 p')

      echo $line

      if  [[ $OUTPUT == *"OK"* ]];
      then
          echo "$OUTPUT" certificate has been registered
      elif [[ $OUTPUT == *"CERTIFICATE_ALREADY_REGISTERED"* ]];
      then
        echo "$OUTPUT" the certificate has already been registered and is being skipped
      else
        echo "$OUTPUT" certificate registeration failed
                STATUS=1
                break
      fi
  done < "./register-cert.txt"
}


register_functions(){
  if [[ $IST_INSTALL_FUNCTIONS == true ]];
  then
    echo registering functions...
    while IFS= read line;
    do
      OUTPUT=$($line --properties $CLIENT_PROPERTIES_PATH | sed -n '2 p')

      echo $line

      if  [[ $OUTPUT == *"OK"* ]];
      then
        echo "$OUTPUT" the function has been registered or did already exist
      else
         echo "$OUTPUT" the function registeration failed
                STATUS=1
                break
      fi
    done < "./register-functions.txt"
  fi
}

register_contracts(){
  if [[ $IST_INSTALL_CONTRACTS == true ]];
  then
    echo registering contracts...
    while IFS= read line;
    do
      OUTPUT=$($line --properties $CLIENT_PROPERTIES_PATH | sed -n '2 p')

      echo $line

      if  [[ $OUTPUT == *"OK"* ]];
      then
        echo "$OUTPUT" the contract has been registered
      elif [[ $OUTPUT == *"CONTRACT_ALREADY_REGISTERED"* ]];
      then
        echo "$OUTPUT" the contract has already been registered and is being skipped
      else
        echo "$OUTPUT" the contract registeration failed
        STATUS=1
        break
      fi
    done < "./register-contracts.txt"
  fi
}

register_cert
if [[ $STATUS == 1 ]];
then
  exit $STATUS
fi
register_functions
if [[ $STATUS == 1 ]];
then
  exit $STATUS
fi
register_contracts
exit $STATUS


