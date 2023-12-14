void parseFields(const char *input, char *fields[], int num, int *fieldIDs, char separator);
void parseLines(const char *input, char *lines[], int entries_count);
int countLines(char *file_path);
char *parse(const char *file_path);