#!/bin/bash

echo "You are at location $PWD"
PS3="Select an option: "
options=("Create Corona infection diagrams" "Move PNG files to location" "Quit")
select menu in "${options[@]}";
do
    if [ "$menu" == "Create Corona infection diagrams" ]; then
                ./coronaprogram
                echo "Executing the program..."
    elif [ "$menu" == "Move PNG files to location" ]; then
                echo "Please name folder to move the PNG files:"
                read filename
                mkdir -p "./$filename"
                mv *.png "./$filename"
                returned=$(ls -lR ./$filename/*.png | wc -l)      
                echo "$returned PNGs are now stored in folder $filename"
    elif [ "$menu" == "Quit" ]; then
            exit
    else
        echo "invalid option $REPLY"
    fi
done