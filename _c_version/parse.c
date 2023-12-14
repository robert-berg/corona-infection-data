#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdarg.h>
#include "parse.h"

/**
 * The function `parseFields` takes an input string, separates it into fields based on a specified
 * separator, and stores the fields in an array.
 * 
 * @param input A pointer to a character array containing the input string.
 * @param fields An array of char pointers that will store the parsed fields.
 * @param num The parameter "num" represents the number of fields that need to be parsed from the input
 * string.
 * @param fieldIDs The `fieldIDs` parameter is an array of integers that specifies the indices of the
 * fields to be extracted from the input string.
 * @param separator The separator is a character that is used to separate fields in the input string.
 */
void parseFields(const char *input, char *fields[], int num, int *fieldIDs, char separator)
{
    char *start, *end;
    unsigned count = 0;

    start = end = (char *)input;

    while ((end = strchr(start, separator)))
    {
        for (int i = 0; i < num; i += 1)
        {

            if (count == fieldIDs[i])
            {

                char dest[10000];

                memcpy(dest, start, (int)(end - start + 1));
                dest[(int)(end - start)] = '\0';

                fields[i] = malloc(strlen(dest) + 1);
                strcpy(fields[i], dest);
            }
        }

        count += 1;
        start = end + 1;
    }

    for (int i = 0; i < num; i += 1)
    {
        if (count == fieldIDs[i])
        {
            fields[i] = malloc(strlen(start) + 1);
            strcpy(fields[i], start);
        }
    }
}

/**
 * The function `parseLines` takes a string input and splits it into separate lines, storing each line
 * in an array of strings.
 * 
 * @param input The input parameter is a pointer to a character array, which represents the input
 * string containing multiple lines of text.
 * @param lines An array of char pointers that will store the parsed lines from the input string.
 * @param entries_count The parameter `entries_count` represents the number of entries or lines in the
 * input string `input`.
 */
void parseLines(const char *input, char *lines[], int entries_count)
{

    char *start, *end;
    unsigned count = 0;

    start = end = (char *)input;

    while ((end = strchr(start, '\n')))
    {

        char dest[10000];

        memcpy(dest, start, (int)(end - start + 1));
        dest[(int)(end - start + 1)] = '\0';
        if (count > 0)
        {
            lines[count - 1] = malloc(strlen(dest) + 1);
            strcpy(lines[count - 1], dest);
        }

        count += 1;
        start = end + 1;
    }

    lines[count - 1] = malloc(strlen(start) + 1);
    strcpy(lines[count - 1], start);
}

/**
 * The function countLines takes a file path as input and returns the number of lines in the file.
 * 
 * @param file_path The file path is a string that specifies the location of the file you want to count
 * the lines of. It should include the file name and extension. For example, "C:/Documents/myfile.txt"
 * or "data/input.csv".
 * 
 * @return the number of lines in the file specified by the file_path parameter.
 */
int countLines(char *file_path)
{

    FILE *my_file = fopen(file_path, "r");

    int c;
    int count = 0;

    for (c = getc(my_file); c != EOF; c = getc(my_file))
        if (c == '\n')
        {
            count = count + 1;
        }

    return count;
}

/**
 * The function `parse` reads the contents of a file and returns a dynamically allocated buffer
 * containing the file's contents.
 * 
 * @param file_path The file path is a string that specifies the location of the file that you want to
 * parse. It should include the file name and its extension.
 * 
 * @return a pointer to a character array (string) that contains the contents of the file specified by
 * the file_path parameter.
 */

char *parse(const char *file_path)
{
    FILE *my_file = fopen(file_path, "r");

    char *buffer = NULL;
    size_t len;

    fseek(my_file, 0, SEEK_END);
    len = ftell(my_file);
    rewind(my_file);
    buffer = (char *)malloc(len + 1);
    if (NULL == buffer)
    {
        exit(-1);
    }

    fread(buffer, 1, len, my_file);
    fclose(my_file);
    buffer[len] = '\0';

    return buffer;
}
