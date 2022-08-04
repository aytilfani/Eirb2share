#ifndef _PARSING_H_
#define _PARSING_H_

#include <stdio.h>
#include <stdlib.h>
#include <regex.h>
#include <string.h>

struct parser
{
    regex_t *announce_exp;
    regex_t *announce_leech_exp;
    regex_t *look_exp;
    regex_t *getfile_exp;
    regex_t *update_compil;
};

enum msg{ANNOUNCE=1, LOOK, GETFILE, LEECH, UPDATE};

int announce_compil(regex_t *exp, regex_t *leech_exp);

int look_compil(regex_t *exp);

int getfile_compil(regex_t *exp);

int init_parser(struct parser *parser);

int verify_msg(char *msg, struct parser *p);

void free_parser(struct parser *p);

#endif