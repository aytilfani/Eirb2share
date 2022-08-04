tracker: tracker.c tracker.h parsing.c parsing.h utils.h utils.c
	gcc tracker.c tracker.h parsing.c parsing.h utils.h utils.c -o tracker -pthread -std=c99 -DMAX_THREAD=1

debug:
	gcc tracker.c tracker.h parsing.c parsing.h utils.h utils.c -o tracker -pthread -std=c99 -DMAX_THREAD=5 -DDEBUG

clean:
	rm tracker log.txt
