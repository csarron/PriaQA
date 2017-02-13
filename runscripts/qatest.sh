#!/bin/bash
#awk -F '\t' '{print $2}' WikiQA-train.tsv | uniq > WikiQATrain-questions.txt
dateStr="date +%s%3N"
os=`uname`
if [[ "$os" == "Darwin" ]]; then
    # macOS, required gdate
    hash gdate 2>/dev/null || {
        echo >&2 "$0: [ERROR] gdate is not installed.
        try: brew install coreutils if you have homebrew"
        exit 1
    }
    dateStr="gdate +%s%3N"
fi

filename="$1"
echo "" > qa-result.txt
i=1
while read -r line
do 
    question="$line"
    echo "Question $i: - $question" |  tee -a qa-result.txt
    ts=$(eval ${dateStr}) ;
    ./query.sh "$question" |  tee -a qa-result.txt
    tt=$((($(eval ${dateStr}) - $ts))) ;
    echo "$tt ms" |  tee -a qa-result.txt
    
#	(time ./sirius-qa-test.sh "$question") 2>&1 |  tee -a qa-result.txt
	echo $'\n' |  tee -a qa-result.txt
    i=$[i+1]

done < "$filename"


