#!/bin/bash
input=$1
source=$2
dest=$3

echo "$source"
echo "$input"
echo "$dest"

while IFS= read -r line
do
    echo "$line";
	cp --parents "$line" "$dest"
	
done < $input
