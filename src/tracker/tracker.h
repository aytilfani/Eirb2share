#ifndef _TRACKER_H_
#define _TRACKER_H_

#include <stdio.h>
#include <stdlib.h>
#include <strings.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <stddef.h>
#include <stdint.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "parsing.h"

#include <sys/stat.h>
#include <fcntl.h>
#include <sys/select.h>

#ifndef MAX_THREAD
#define MAX_THREAD 1
#endif
#ifndef MAX_ITER
#define MAX_ITER 5
#endif
#ifndef MAX_PEER
#define MAX_PEER 20
#endif
#ifndef MAX_MESSAGE_FAILED
#define MAX_MESSAGE_FAILED 3
#endif

typedef void *(*thread_func_t)(void * args);
typedef void *(*connect_func_t)(void *, unsigned);

struct thread;

struct work;

struct work_queue;

struct keylist;

void message_received(int, intptr_t, char *, enum msg);

#endif