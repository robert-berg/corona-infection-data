#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "parse.h"
#include "country_codes.h"

/**
 * The function `load_country_codes` reads data from a CSV file containing country codes and ISO2
 * codes, and stores them in an array of `country_code` structs.
 * 
 * @param country_codes The parameter `country_codes` is a pointer to an array of `country_code`
 * structs.
 * 
 * @return the number of entries in the country_codes array.
 */
int load_country_codes(country_code *country_codes)
{
    char file_path[] = "./csv-files/CountryCodes.csv";

    int entries_count = countLines(file_path);

    char *buffer = parse(file_path);

    char **lines = malloc(sizeof(int *) * entries_count);

    parseLines(buffer, lines, entries_count);

    for (int i = 0; i < entries_count; i += 1)
    {

        char **fields = malloc(sizeof(int *) * 2);

        parseFields(lines[i], fields, 2, (int[]){9, 10}, ',');

        for (int j = 0; j < 2; j += 1)
        {

            switch (j)
            {
            case 0:
                country_codes[i].code = fields[j];
                break;
            case 1:
                country_codes[i].iso2 = fields[j];
                break;
            }
        }
    }

    return entries_count;
}