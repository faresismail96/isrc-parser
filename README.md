# ISRC Parser

## Objective

This program reads a TSV file from a specified path and parses its content.
The program also validates all the records and aggregates them by title and sums the amount.

If the file is invalid, the program will output a list of errors for each invalid record.

If the file is valid, the program will output a file containing the total amount by title, sorted in descending order.
The program will also output the total amount on the console.

## File Format

The TSV file must have 3 columns with headers: title, isrc and amount.

Title is a mandatory string
amount if a mandatory double
ISRC is an optional string with the following format:
12 characters long
 - First 2 chars represent the country code
 - Next 3 chars represent the registrant code
 - Next 2 chars represent the year of reference
 - Last 5 chars represent the designation code and identifies the particular sound or video recording for a reference year.
 
Example: USRC17607839 for the song "Crazy Eyes" by Daryl Hall & John Oates

## How to run

To run the program, checkout the code in IntelliJ and run. By default, the given file is valid. Point to the invalid file to see a detailed list of errors. 