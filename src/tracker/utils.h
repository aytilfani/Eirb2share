#ifndef _UTILS_H_
#define _UTILS_H_

#include "tracker.h"

void print_init();

void error(char *);

char *int_to_ip(int, char *);

int ip_to_int(char *);

char *readline(FILE *);

int readconfig(int *, int *, char *);

void chartotime(char *, unsigned *, unsigned *);

#endif