#!/bin/bash
filename="$1"
echo "" > qa-result.txt
i=1
while read -r line
do 
    question="$line"
    echo "Question $i: - $question" |  tee -a qa-result.txt
    ts=$(date +%s%3N) ;
    ./query.sh "$question" |  tee -a qa-result.txt
    tt=$((($(date +%s%3N) - $ts))) ; 
    echo "$tt ms" |  tee -a qa-result.txt
    
#	(time ./sirius-qa-test.sh "$question") 2>&1 |  tee -a qa-result.txt
	echo $'\n' |  tee -a qa-result.txt
    i=$[i+1]

done < "$filename"


