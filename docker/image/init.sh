STATUS=0
while IFS= read line;
do
  OUTPUT=$($line | sed -n '2 p')
  if  [[ $OUTPUT != *"OK"* && $OUTPUT != *"CERTIFICATE_ALREADY_REGISTERED"* ]];
  then
    echo "$OUTPUT"
    STATUS=1
    break
  fi
done < "./register.txt"
echo finish
exit $STATUS
