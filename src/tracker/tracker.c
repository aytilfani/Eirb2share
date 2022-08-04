#include "parsing.h"
#include "utils.h"

FILE* logfile;
struct filelist *tracker_files = NULL;
struct peer *client_list = NULL;

unsigned stimeout;
unsigned ustimeout;

struct thread
{
    struct work_queue *thread;
    pthread_t id;
    struct thread *next;
};

struct work_queue
{
    struct work *first;
    struct work *last;
};

struct work
{
    connect_func_t func;
    void *args;
    struct work *next;
    unsigned ip_peer;
};

struct keylist
{
    char key[64];
    struct keylist *next;
};

struct peer
{
    unsigned ip;
    unsigned port;
    struct keylist *leech;
    struct peer *next;
};

struct file
{
    char key[64];
    char filename[64];
    unsigned length;
    unsigned piecesize;
};

struct filelist
{
    struct file *file;
    struct filelist *next;
    struct peer *client[MAX_PEER];
    unsigned nclient;
};

#ifdef DEBUG
void print_files()
{
    printf("\nprint files list\n");
    if (tracker_files == NULL) printf("\033[0;31mempty list\033[0m\n");
    else {
        struct filelist *tmp = tracker_files;
        struct file *file;
        
        while (tmp)
        {
            logfile = fopen("log.txt","a");
            file = tmp->file;
            printf("name : %s | key : %s | length : %d | piecesize : %d\n", file->filename, file->key, file->length, file->piecesize);
            fprintf(logfile, "name : %s | key : %s | length : %d | piecesize : %d\n", file->filename, file->key, file->length, file->piecesize);
            printf("%d client(s) have this file\n", tmp->nclient);
            fprintf(logfile, "%d client(s) have this file\n", tmp->nclient);
            tmp = tmp->next;
            fclose(logfile);
        }
    }
}

void print_client()
{
    char ip[64];
    printf("\nprint client list\n");
    if (client_list == NULL) printf("\033[0;31mempty list\033[0m\n");
    else {
        struct peer *tmpclient = client_list;
        struct keylist *tmpkey;
        while (tmpclient)
        {
            logfile = fopen("log.txt","a");
            tmpkey = tmpclient->leech;
            printf("ip : %s | port : %d\n", int_to_ip(tmpclient->ip, ip), tmpclient->port);
            fprintf(logfile, "ip : %s | port : %d\n", int_to_ip(tmpclient->ip, ip), tmpclient->port);
            printf("leech list :");
            fprintf(logfile, "leech list :");
            fclose(logfile);
            while (tmpkey)
            {
                logfile = fopen("log.txt","a");
                printf("\t%s", tmpkey->key);
                fprintf(logfile, "\t%s", tmpkey->key);
                tmpkey = tmpkey->next;
                fclose(logfile);
            }
            logfile = fopen("log.txt","a");
            printf("\n");
            fprintf(logfile, "\n");
            fclose(logfile);
            tmpclient = tmpclient->next;
        }
    }
}
#endif

void *thread_func(void *args)
{
    struct work_queue *work_queue = (struct work_queue *)args;
    struct work *work_pool = work_queue->first;
    struct work *work;
    while (1)
    {
        work = work_pool->next;
        if (work != NULL)
        {
            work_pool->next = work->next;
            if (work->next == NULL)
                work_queue->last = NULL;
            work->func(work->args, work->ip_peer);
            printf("\n\033[0;90mExécution terminée\033[0m\n");
        }
        else
        {
            sleep(2);
        }
    }
    return NULL;
}

struct peer *find_client(int ip)
{
    struct peer *client = client_list;
    struct peer *tmp = client_list;
    while (client)
    {
        if (client->ip == ip)
            break;
        tmp = client;
        client = client->next;
    }
    if (client == NULL)
    {
        client = malloc(sizeof(struct peer));
        client->ip = ip;
        client->next = NULL;
        if (tmp == NULL)
            client_list = client;
        else
            tmp->next = client;
    }
    return client;
}

void *client_connect(void *args, unsigned ip)
{
    intptr_t newsockfd = (intptr_t) args;
    if (newsockfd < 0) {
        error("ERROR on accept");
        fprintf(logfile, "ERROR on accept");
    }
    char message[256];
    bzero(message, 256);

    struct parser *p = malloc(sizeof(struct parser));
    int i = init_parser(p);
    int n;
    int failed = 0;
    int msglen;
    int current_fail;

    fd_set set;
    struct timeval timeout;
    int rv;
    FD_ZERO(&set);
    FD_SET(newsockfd, &set);

    timeout.tv_sec = stimeout;
    timeout.tv_usec = ustimeout;

    while(failed < MAX_MESSAGE_FAILED)
    {
        msglen = 0;
        current_fail = 0;
        do
        {
            rv = select(newsockfd + 1, &set, NULL, NULL, &timeout);
            if(rv == -1)
                error("select");
            else if(rv == 0)
            {
                failed = MAX_MESSAGE_FAILED;
                current_fail = 1;

                printf("< \033[0;31mNOK\033[0m \033[0;90m(timeout)\033[0m\n");
                n = write(newsockfd, "NOK\n", 5);
                if (n < 0)
                    error("ERROR writing to socket");
            }
            else
            {
                n = read(newsockfd, message + msglen, 255);

                if (n < 0)
                {
                    failed++;
                    current_fail = 1;
                    //error("ERROR reading from socket");
                }
                else if (n == 0)
                {
                    current_fail = 1;
                    failed = MAX_MESSAGE_FAILED;
                }
                else
                    msglen += n;
            }
        } while (!current_fail && message[msglen - 1] != '\n');

        if (!current_fail)
        {
            int rt_msg = verify_msg(message, p);
            message[msglen - 1] = '\0';

            printf("\n> %s\n", message);
            logfile = fopen("log.txt","a");
            fprintf(logfile, "> %s\n", message);
            fclose(logfile);
            if (rt_msg < 0)
            {
                printf("< \033[0;31mNOK\033[0m\n");
                n = write(newsockfd, "NOK\n", 5);
                if (n < 0) {
                    error("ERROR writing to socket");
                }
                failed++;
            }
            else
            {
                message_received(ip, newsockfd, message, rt_msg);
            }
        }
    }
    free_parser(p);

    return NULL;
}

void message_received(int ip, intptr_t newsockfd, char *message, enum msg rt_msg)
{
    int n;

    if (rt_msg == ANNOUNCE || rt_msg == LEECH)
    {
        struct peer *client = find_client(ip);
        char *strtoken = strtok(message, " ");
        for (int i = 0; i < 2; i++)
            strtoken = strtok(NULL, " ");
        client->port = atoi(strtoken);
        for (int i = 0; i < 2; i++)
            strtoken = strtok(NULL, " ");

        int seeding = 1;
        while ((strtoken != NULL) && seeding)
        {
            struct file *newfile = malloc(sizeof(struct file));
            char tmpchar[64];
            strcpy(tmpchar, strtoken);
            if (tmpchar[0] == '['){
                strcpy(newfile->filename, tmpchar + 1);
            }
            else{
                strcpy(newfile->filename, tmpchar);
            }
            strtoken = strtok(NULL, " ");
            newfile->length = atoi(strtoken);

            strtoken = strtok(NULL, " ");
            newfile->piecesize = atoi(strtoken);

            strtoken = strtok(NULL, " ");
            strcpy(tmpchar, strtoken);
            int len = strlen(tmpchar);
            if (tmpchar[len - 1] == '\n')
            {
                tmpchar[len - 2] = '\0';
                seeding = 0;
            }
            else if (tmpchar[len - 1] == ']')
            {
                tmpchar[len - 1] = '\0';
                seeding = 0;
            }
            strcpy(newfile->key, tmpchar);

            struct filelist *tmp = tracker_files;
            int stop = 0;
            while (tmp && !stop)
            {
                if (!strcmp(tmp->file->key, newfile->key))
                {
                    int i = 0;
                    while (i < tmp->nclient && tmp->client[i++]->ip != client->ip)
                        ;
                    if (i == tmp->nclient)
                        tmp->client[tmp->nclient++] = client;
                    stop = 1;
                }
                tmp = stop ? tmp : tmp->next;
            }
            if (tmp == NULL)
            {
                tmp = malloc(sizeof(struct filelist));
                tmp->file = newfile;
                tmp->client[0] = client;
                tmp->nclient = 1;
                tmp->next = tracker_files;
                tracker_files = tmp;
            }
            else
            {
                free(newfile);
            }

            strtoken = strtok(NULL, " ");
        }
        if (rt_msg == LEECH)
        {
            int leeching = 1;
            char tmpchar[64];
            int tmplen;
            if (client->leech != NULL)
            {
                client->leech = NULL;
            }
            while (leeching)
            {
                struct keylist *newleech = malloc(sizeof(struct keylist));
                newleech->next = client->leech;
                strtoken = strtok(NULL, " ");
                strcpy(tmpchar, strtoken);
                tmplen = strlen(tmpchar);
                if (tmpchar[tmplen - 1] == '\n')
                {
                    tmpchar[tmplen - 2] = '\0';
                    leeching = 0;
                }
                else if (tmpchar[tmplen - 1] == ']')
                {
                    tmpchar[tmplen - 1] = '\0';
                    leeching = 0;
                }
                if (tmpchar[0] == '['){
                    strcpy(newleech->key, tmpchar + 1);
                }
                else{
                    strcpy(newleech->key, tmpchar);
                }
                struct keylist *tmpkeylist = client->leech;
                client->leech = newleech;
            }
        }
        logfile = fopen("log.txt", "a");
	fprintf(logfile, "< OK\n");
	fclose(logfile);
	printf("< \033[0;32mOK\033[0m\n");
        n = write(newsockfd, "OK\n", 4);
        if (n < 0)
            error("ERROR writing to socket");
        // Pour le moment on a implémenté juste le critère d'égalité de nom
    }
    else if (rt_msg == LOOK)
    {
        char file_to_look[64];
        char *token = malloc(256);
        token = strtok(message, " ");
        token = strtok(NULL, " ");
        char tmpchar[64];
        strcpy(tmpchar, token);
        // printf("%s\n",tmpchar);
        int tmplen = strlen(tmpchar);
        if (tmpchar[tmplen - 1] == ']')
            --tmplen;
        tmpchar[tmplen - 5] = '\0';
        strcpy(file_to_look, tmpchar + 12 + (tmpchar[0] == '['));
        // printf("%s\n",file_to_look);
        struct filelist *tmp = tracker_files;
        logfile = fopen("log.txt", "a");
        printf("< list [");
        fprintf(logfile, "< list [");
        n = write(newsockfd, "list [", 6);
        if (n < 0)
            error("ERROR writing to socket");
        char *msg = malloc(256);
        bzero(msg,256);
        while (tmp)
        {
            if (!strcmp(tmp->file->filename, file_to_look))
            {
                sprintf(msg, "%s %d %d %s ", tmp->file->filename, tmp->file->length, tmp->file->piecesize, tmp->file->key);
                fprintf(logfile, "%s\n", msg);
                printf("%s", msg);
                n = write(newsockfd, msg, strlen(msg));
                if (n < 0)
                    error("ERROR writing to socket");
                break;
            }
            tmp = tmp->next;
        }
        free(msg);
        // free(token);
        printf("]\n");
        n = write(newsockfd, "]\n", 3);
        fprintf(logfile, "]\n");
        fclose(logfile);
        if (n < 0)
            error("ERROR writing to socket");
    }
    else if (rt_msg == GETFILE)
    {
        logfile = fopen("log.txt", "a");
        char *key = malloc(256);
        char *msg = malloc(256);
        bzero(msg, 256);
        key = strtok(message, " ");
        key = strtok(NULL, " ");
        int lenkey = strlen(key);
        if (key[lenkey - 1] == '\n');
            key[lenkey - 1] = '\0';
        struct filelist *tmp = tracker_files;
        printf("< peers %s [", key);
        sprintf(msg, "peers %s [", key);
        fprintf(logfile, "< %s\n", msg);
        n = write(newsockfd, msg, strlen(msg));
        if (n < 0)
            error("ERROR writing to socket");
        while (tmp)
        {
            if (strcmp(tmp->file->key, key)!=0)
            {
                printf("%s\n %s \n\n",tmp->file->key,key );
                tmp = tmp->next;
            }
            else
            {
                struct peer **clients = tmp->client;
                for (int i = 0; i < tmp->nclient; ++i)
                {
                    char ip[64];
                    int_to_ip(clients[i]->ip, ip);
                    sprintf(msg, "%s:%d ", ip, 2021);
                    printf("%s", msg);
                    fprintf(logfile, "%s\n", msg);
                    n = write(newsockfd, msg, strlen(msg) - (i == tmp->nclient - 1));
                    if (n < 0)
                        error("ERROR writing to socket");
                }
                break;
            }

        }
        fclose(logfile);
        free(msg);
        // free(key);
        printf("]\n");
        n = write(newsockfd, "]\n", 2);
        if (n < 0)
            error("ERROR writing to socket");
    }
    else
    {
        error("ERROR parser");
    }
    

    #ifdef DEBUG
    print_files();

    print_client();
    #endif

    return;
}

int main(int argc, char *argv[])
{
    print_init();

    char date[200];
    logfile = fopen("log.txt","w");
    time_t now = time(NULL);
    struct tm *t = localtime(&now);
    strftime(date, sizeof(date), "%d %m %Y %H:%M", t);
    fprintf(logfile, "Log file created on %s\n", date);  
    intptr_t sockfd;
    int portno = -1;
    struct sockaddr_in serv_addr;
    serv_addr.sin_addr.s_addr = 0;
    char *timeout = malloc(256);

    int ret = readconfig(&portno, &serv_addr.sin_addr.s_addr, timeout);
    if (ret < 0)
    {
        fprintf(stderr, "ERROR, wrong config\n");
        exit(1);
    }
    if (portno < 0)
    {
        fprintf(stderr, "ERROR, no port provided\n");
        exit(1);
    }
    if (argc > 1)
    {
        portno = atoi(argv[1]);
    }
    chartotime(timeout, &stimeout,  &ustimeout);
    free(timeout);

    fprintf(logfile, "Command timeout set to %ds %dus\n", stimeout, ustimeout);
    fclose(logfile);

    sockfd = socket(AF_INET, SOCK_STREAM, 0);

    if (sockfd < 0)
        error("ERROR opening socket");

    serv_addr.sin_family = AF_INET;
    if (!serv_addr.sin_addr.s_addr)
    {
        serv_addr.sin_addr.s_addr = INADDR_ANY;
    }
    serv_addr.sin_port = htons(portno);

    if (bind(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR on binding");

    struct thread *free_threads = NULL;

    struct work *first_work = malloc(sizeof(struct work));
    first_work->next = NULL;
    struct work_queue *work_pool = malloc(sizeof(struct work_queue));
    work_pool->first = first_work;
    work_pool->last = NULL;

    /* initialise la liste simplement chaînée*/
    for (int i = 0; i < MAX_THREAD; i++)
    {
        struct thread *next_thread = malloc(sizeof(struct thread));
        next_thread->next = free_threads;
        next_thread->id = i;
        next_thread->thread = work_pool;
        free_threads = next_thread;
    }

    struct thread *threads = free_threads;
    thread_func_t init_func = &thread_func;
    while (threads)
    {
        pthread_create(&(threads->id), NULL, init_func, (void *)threads->thread);
        threads = threads->next;
    }

    struct sockaddr_in cli_addr;
    int clilen = sizeof(cli_addr);

    listen(sockfd, 5);
    struct work *newtask = malloc(sizeof(struct work));
    newtask->func = client_connect;
    newtask->next = NULL;

    intptr_t newsockfd;
    char ip[64];
    printf("\033[0;32mServeur démarré sur le port %d avec l'adresse %s\033[0m\n", portno, int_to_ip(serv_addr.sin_addr.s_addr, ip));

    while (1)
    {
        newsockfd = accept(sockfd, (struct sockaddr *)&cli_addr, &clilen);
        newtask->ip_peer = cli_addr.sin_addr.s_addr;
        printf("\033[0;94mNouvelle connexion de \033[0;34m%s\033[0m\n\n", int_to_ip(newtask->ip_peer, ip));
        newtask->args = (void *)newsockfd;
        if (work_pool->last != NULL)
            work_pool->last->next = newtask;
        else
            work_pool->first->next = newtask;
        work_pool->last = newtask;
    }

    while (free_threads)
    {
        pthread_join(free_threads->id, NULL);
        free_threads = free_threads->next;
    }
    return 0;
}
