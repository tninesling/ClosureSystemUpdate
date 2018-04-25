#define _FILE2_LOAD_FROM_MEMORY_
#include "shd.c"


int a[11] = {1,2,3,INTHUGE,2,4,INTHUGE,1,3,4,INTHUGE-1}, *buf;
int b[11] = {2,4,3,INTHUGE,2,5,INTHUGE,1,2,4,INTHUGE-1};

main (int argc, char *argv[]){
  __load_from_memory_org__ = b;
  EXECSUB (SHD_main, 0, exit, "shd 0 void void", 0);
  buf = __write_to_memory_org__;
  while (*buf != INTHUGE-1){
    printf ("%d\n", *buf);
    buf++;
  }
  printf("============\n");
  __load_from_memory_org__ = a;
  EXECSUB (SHD_main, 0, exit, "shd 0 void void", 0);
  buf = __write_to_memory_org__;
  while (*buf != INTHUGE-1){
    printf ("%d\n", *buf);
    buf++;
  }
}



