#include "utils.h"

#define COLOREIRB "\033[0;94m"
#define COLOR2 "\033[0;31m"
#define COLORSHARE "\033[0;32m"
#define NOCOLOR "\033[0m"

void print_init()
{
    printf("%s ______  _        _    %s ___   %s _____  _                         %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);                     
    printf("%s|  ____|(_)      | |   %s|__ \\ %s / ____|| |                       %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);                     
    printf("%s| |__    _  _ __ | |__ %s   ) |%s| (___  | |__    __ _  _ __  ___  %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);
    printf("%s|  __|  | || '__|| '_ \\%s  / / %s \\___ \\ | '_ \\  / _` || '__|/ _ \\ %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);
    printf("%s| |____ | || |   | |_) |%s/ /_  %s____) || | | || (_| || |  |  __/ %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);
    printf("%s|______||_||_|   |_.__/%s|____|%s|_____/ |_| |_| \\__,_||_|   \\___| %s\n", COLOREIRB, COLOR2, COLORSHARE, NOCOLOR);

    printf("\n\t\tA P2P file sharing system (2022)\n\n");
}

void error(char *msg)
{
    perror(msg);
    exit(1);
}

char *int_to_ip(int ip_int, char *ip_char)
{
    int a = 0, b =0, c = 0, d = 0;
    a = (ip_int & 0xFF000000) / 0x01000000;
    b = (ip_int & 0x00FF0000) / 0x00010000;
    c = (ip_int & 0x0000FF00) / 0x00000100;
    d = (ip_int & 0x000000FF);
    sprintf(ip_char, "%d.%d.%d.%d", d, c, b, a);
    return ip_char;
}

int ip_to_int(char *ip_char)
{
    int ip = 0;
    ip += atoi(strtok(ip_char, "."));
    ip += atoi(strtok(NULL, ".")) * 0x00000100;
    ip += atoi(strtok(NULL, ".")) * 0x00010000;
    ip += atoi(strtok(NULL, "\0")) * 0x01000000;

    return ip;
}

char *readline(FILE *file)
{
    char *msg = malloc(256);
    int len = 0;
    int n;
    do {
        n = fread(msg + len++, 1, 1, file);
        if (!n)
        {
            return NULL;
        }
        if (n < 0)
            error("ERROR reading from config file");
    } while(msg[len - 1] != '\n');

    msg[len] = '\0';
    return msg;
}

int readconfig(int *port, int *ip, char *time)
{
    FILE *config = fopen("config.ini", "r");
    char *line = readline(config);
    char *token; 
    while(line != NULL) {
        char *token = strtok(line, " ");
        if (!strcmp(token, "tracker-port"))
        {
            if (strcmp("=", strtok(NULL, " ")))
            {
                return -1;
            }
            char *tok = strtok(NULL, "\n");
            if (tok != NULL)
                *port = atoi(tok);
        }
        else if (!strcmp(token, "tracker-address"))
        {
            if (strcmp("=", strtok(NULL, " ")))
            {
                return -1;
            }
            char *tok = strtok(NULL, "\n");
            if (tok != NULL)
                *ip = ip_to_int(tok);
        }
        else if (!strcmp(token, "max-timeout"))
        {
            if (strcmp("=", strtok(NULL, " ")))
            {
                return -1;
            }
            char *tok = strtok(NULL, "\n");
            if (tok != NULL)
                strcpy(time, tok);
        }
        free(line);
        line = readline(config);
    }
    fclose(config);
    return 0;
}

void chartotime(char *c, unsigned *s, unsigned *us)
{
    char *tok = strtok(c, "s");
    int toklen = strlen(tok);
    if (tok[toklen - 1] == 'u')
    {
        tok[toklen - 1] = '\0';
        *s = 0;
        *us = atoi(tok);
    }
    else
    {
        char *stok = strtok(tok, ".");
        *s = atoi(stok);
        stok = strtok(NULL, "\n");
        if (stok)
        {
            int stoklen = strlen(stok);
            int a = atoi(stok);
            for (int i = 0; i < 6 - stoklen; ++i)
                a *= 10;
            *us = a;
        }
        else
            *us = 0;
    }
    return;
}