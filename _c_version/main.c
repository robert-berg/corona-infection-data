#include "population_data.h"
#include "country_codes.h"
#include "parse.h"
#include "pbPlots.h"
#include "supportLib.h"
#include <sys/types.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

/**
 * It generates a random number between min and max
 * 
 * @param min The minimum number that can be generated.
 * @param max The maximum number of the range.
 * 
 * @return a random number between min and max.
 */
unsigned int rand_interval(unsigned int min, unsigned int max)
{
  /* Generating a random number between min and max. */
    srand(getpid());
    int r;
    const unsigned int range = 1 + max - min;
    const unsigned int buckets = RAND_MAX / range;
    const unsigned int limit = buckets * range;

    do
    {
        r = rand();
    } while (r >= limit);

    return min + (r / buckets);
}


int main(void)
{
    /* Allocating memory for the country_codes and population_entries. */
    country_code *country_codes;
    country_codes = (country_code *)malloc(sizeof(country_code) * countLines("./csv-files/CountryCodes.csv"));
    int country_count = load_country_codes(country_codes);
    population_entry *population_entries;
    population_entries = (population_entry *)malloc(sizeof(population_entry) * countLines("./csv-files/UN.csv"));
    int population_data_count = load_population_data(population_entries);

    /* Declaring a string variable called file_path and assigning it the value "./csv-files/WHO.csv". */
    char file_path[] = "./csv-files/WHO.csv";

    /* Parsing the WHO.csv file and storing the data in the lines variable. */
    int entries_count = countLines(file_path);
    char *buffer = parse(file_path);
    char **lines = malloc(sizeof(int *) * entries_count);
    parseLines(buffer, lines, entries_count);

    /* Creating a pipe and checking if it was created successfully. */
    int fd[2];
    int depth = 0;
    int process_count = 0;
    if (pipe(fd) != 0)
    {
        printf("Failed to create pipe\n");
        return 1;
    }

   /* Creating child processes. */
    for (int i = 0; i < 5; i += 1)
    {
        int pid = fork();

        /* Checking if the process is a child process. */
        if (pid == 0)
        {
            /* Writing the value of i to the pipe. */
            write(fd[1], &i, 1);
            /* Incrementing the depth variable by 1. */
            depth += 1;
        }
    }
    
    /* Closing the write end of the pipe. */
    close(fd[1]);
    if (depth == 0)
    {
        process_count = 0;
        while (read(fd[0], &depth, 1) != 0)
            process_count += 1;
        printf("\n%d total processes\n", process_count);
    }

    else
    {

        /* Generating a random number between 1 and 245. */
        int r = rand_interval(1, 245);


        /* Creating a scatter plot arandom country. */
        for (int j = 0; j < population_data_count; j += 1)
        {

            /* Checking if the country code of the population entry is equal to the country code of the country
            code. */
            if (atoi(population_entries[j].code) == atoi(country_codes[r].code))
            {

               /* Counting the number of entries in the WHO.csv file that have the same country code as
               the country code of the country that was randomly selected. */
                int count = 0;
                for (int k = 0; k < entries_count; k += 1)
                {
                    char **fields = malloc(sizeof(int *) * 2);

                    parseFields(lines[k], fields, 2, (int[]){1, 4}, ',');
                    if (
                        strcmp(fields[0], country_codes[r].iso2) == 0)
                    {
                        count += 1;
                    }
                }

                /* Allocating memory for the x and y values of the scatter plot. */
                double *y = malloc(sizeof(double) * count);
                double *x = malloc(sizeof(double) * count);

                count = 0;
                for (int k = 0; k < entries_count; k += 1)
                {
                    char **fields = malloc(sizeof(int *) * 2);

                    parseFields(lines[k], fields, 2, (int[]){1, 4}, ',');
                    if (
                        strcmp(fields[0], country_codes[r].iso2) == 0)
                    {
                        /* Converting the string to an integer and then converting the integer to a double. */
                        y[count] = (double)atoi(fields[1]);
                        /* Assigning the value of count to the x array. */
                        x[count] = (double)count;

                        count += 1;
                    }
                }

                _Bool success;
                StringReference *errorMessage;


                /* Creating a scatter plot. */
                RGBABitmapImageReference *imageRef = CreateRGBABitmapImageReference();
                errorMessage = (StringReference *)malloc(sizeof(StringReference));
                success = DrawScatterPlot(imageRef, 800, 600, x, 100000, y, 100000, errorMessage);

                if (success)
                {
                    /* Converting the image to a png file and then writing it to a file. */
                    size_t length;
                    /* Converting the image to a png file. */
                    double *pngdata = ConvertToPNG(&length, imageRef->image);
                    DeleteImage(imageRef->image);

                    /* Creating a file name for the png file that is going to be created. */
                    char file_name[80];
                    sprintf(file_name, "iso_%s.png", country_codes[r].iso2);

                    /* Writing the png data to a file. */
                    WriteToFile(pngdata, length, file_name);
                }
                else
                {
                    /* Printing an error message to the standard error stream. */
                    fprintf(stderr, "Error ");
 
                }
            }
        }
    }
    return 0;
}