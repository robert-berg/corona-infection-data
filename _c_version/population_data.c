#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "parse.h"
#include "population_data.h"

/**
 * The function `load_population_data` reads population data from a CSV file and stores it in an array
 * of `population_entry` structures.
 * 
 * @param population_entries population_entries is a pointer to an array of population_entry
 * structures.
 * 
 * @return the number of entries in the population data.
 */
int load_population_data(population_entry *population_entries)
{
    char file_path[] = "./csv-files/UN.csv";

    int entries_count = countLines(file_path);

    char *buffer = parse(file_path);

    char **lines = malloc(sizeof(int *) * entries_count);

    parseLines(buffer, lines, entries_count);

    for (int i = 0; i < entries_count; i += 1)
    {

        char **fields = malloc(sizeof(int *) * 2);

        parseFields(lines[i], fields, 2, (int[]){0, 3}, ';');

        for (int j = 0; j < 2; j += 1)
        {

            switch (j)
            {
            case 0:
                fields[j]++;
                fields[j][strlen(fields[j]) - 1] = 0;
                population_entries[i].code = fields[j];
                break;
            case 1:
                fields[j]++;
                fields[j][strlen(fields[j]) - 1] = 0;
                population_entries[i].population = atof(fields[j]);

                break;
            }
        }
    }

    return entries_count;
}