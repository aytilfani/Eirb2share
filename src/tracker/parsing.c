#include "parsing.h"

// announce avec leech d'une plusieurs clés marche reste à voir comment rendre l'expression
// régulière assez générale pour qu'elle traîte le cas sans leech
int announce_compil(regex_t *exp, regex_t *leech_exp)
{
    // announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]
    char str_announce[] = "^announce listen (([[:digit:]]+)) seed \\[(([[:graph:]]+ [[:digit:]]+ [[:digit:]]+ [[:lower:][:digit:]]+)?( [[:graph:]]+ [[:digit:]]+ [[:digit:]]+ [[:lower:][:digit:]]+)*)\\] leech \\[([[:lower:][:digit:]]+)?( [[:lower:][:digit:]]+)*\\][^.]+$";
    char str_announce_bis[] = "^announce listen (([[:digit:]]+)) seed \\[(([[:graph:]]+ [[:digit:]]+ [[:digit:]]+ [[:lower:][:digit:]]+)?( [[:graph:]]+ [[:digit:]]+ [[:digit:]]+ [[:lower:][:digit:]]+)*)\\][^.]+$";
    if (regcomp(exp, str_announce_bis, REG_EXTENDED) == 0 && regcomp(leech_exp, str_announce, REG_EXTENDED) == 0)
    {
        return 0;
    }
    return -1;
}

int look_compil(regex_t *exp)
{
    // look [filename=”file_a.dat” filesize>”1048576”]
    const char str_look[] = "^look \\[filename=”([[:graph:]]+)”][^.]+$";
    if (regcomp(exp, str_look, REG_EXTENDED) == 0)
    {
        return 0;
    }
    return -1;
}

int getfile_compil(regex_t *exp)
{
    // getfile 8905e92afeb80fc7722ec89eb0bf0966
    const char str_getfile[] = "^getfile (([[:lower:][:digit:]]+))[^.]+$";
    if (regcomp(exp, str_getfile, REG_EXTENDED) == 0)
    {
        return 0;
    }
    return -1;
}

int update_compil(regex_t *exp) {
    const char str_update[] = "^update seed \\[([[:lower:][:digit:] ]*)\\] leech \\[(([[:lower:][:digit:] ]*))\\][^.]+$";
    if (regcomp(exp, str_update, REG_EXTENDED) == 0)
    {
        return 0;
    }
    return -1;
}

int init_parser(struct parser *parser)
{
    parser->announce_exp = malloc(sizeof(regex_t));
    parser->announce_leech_exp = malloc(sizeof(regex_t));
    parser->getfile_exp = malloc(sizeof(regex_t));
    parser->look_exp = malloc(sizeof(regex_t));
    parser->update_compil = malloc(sizeof(regex_t));
    int i, j, k, l;
    i = announce_compil(parser->announce_exp, parser->announce_leech_exp);
    j = look_compil(parser->look_exp);
    k = getfile_compil(parser->getfile_exp);
    l = update_compil(parser->update_compil);
    return i && j && k && l;
}

int verify_msg(char *msg, struct parser *p)
{
    char start_pattern[9];
    strncpy(start_pattern, msg, 9);
    strtok(start_pattern, " ");
    if (!regexec(p->getfile_exp, msg, 0, NULL, 0))
    {
        return GETFILE;
    }
    else if (!regexec(p->look_exp, msg, 0, NULL, 0))
    {
        return LOOK;
    }
    else if (!regexec(p->announce_exp, msg, 0, NULL, 0))
    {
        return ANNOUNCE;
    }
    else if (!regexec(p->announce_leech_exp, msg, 0, NULL, 0))
    {
        return LEECH;
    }
    else if (!regexec(p->update_compil, msg, 0, NULL, 0))
    {
        return UPDATE;
    }
    else 
    {
        return -1;
    }
}


void free_parser(struct parser *p)
{
    regfree(p->announce_exp);
    free(p->announce_exp);
    regfree(p->announce_leech_exp);
    free(p->announce_leech_exp);
    regfree(p->getfile_exp);
    free(p->getfile_exp);
    regfree(p->look_exp);
    free(p->look_exp);
    free(p);
}

/*
int main()
{
    // announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e
    char msg1[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech [330a57722ec8b0bf09669a2b35f88e9e 330a57722ec8b0bf09669a2b35f88e7e]\n";
    char msg2[] = "getfile 8905e92afeb80fc7722ec89eb0bf096\n";
    char msg3[] = "getfile announce 2222 listen file_a.dat\n";
    char msg7[] = "look [filename=”file_a.dat”]\n";
    char msg5[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e] leech [330a57722ec8b0bf09669a2b35f88e9e]\n";
    char msg6[] = "announce listen 2222 seed [file_a.dat 2097152 1024 8905e92afeb80fc7722ec89eb0bf0966 file_b.dat 3145728 1536 330a57722ec8b0bf09669a2b35f88e9e]\n";
    char msg8[] = "update seed [] leech [8905e92afeb80fc7722ec89eb0bf0966]\n";
    struct parser *p = malloc(sizeof(struct parser));
    int n = init_parser(p);
    if (verify_msg(msg1, p) != -1)
        printf("valid msg1\n");
    if (verify_msg(msg2, p) != -1)
        printf("valid msg2\n");
    if (verify_msg(msg3, p) == -1)
        printf("invalid msg3\n");
    if (verify_msg(msg5, p) != -1)
        printf("valid msg5\n");
    if (verify_msg(msg6, p) != -1)
        printf("valid msg6\n");
    if (verify_msg(msg7, p) != -1) 
        printf("valid msg7\n");
    if (verify_msg(msg8, p) != -1)
        printf("valid msg8\n");
    free_parser(p);
    return 0;
}*/

