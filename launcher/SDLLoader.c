
#include <stdlib.h>
#include <stdio.h>

#ifdef __ANDROID__
#define SDL_DISABLE_IMMINTRIN_H 1
#endif

#include <SDL.h>

typedef int (*SDL_main_func)(int argc,char *argv[]);

int SDL_main(int argc,char *argv[]){
    FILE *flatCfgFile=fopen("./res/flat","r");
    char *buf2=(char *)malloc(0x100);
    char *buf=(char *)malloc(0x100);
    char *pch;
    char *pch2;
    void *dll;
    int returnFromSdl=0;
    SDL_main_func entry_func;
    
    fscanf(flatCfgFile,"%s",buf2);
    fscanf(flatCfgFile,"%s",buf2);
    fscanf(flatCfgFile,"%s",buf2);
    fclose(flatCfgFile);
    pch2=buf;
    for(pch=buf2;*pch!=0&&(pch-buf2<0x100);pch++){
        if(*pch=='\\'){
            pch++;
            if(*pch=='s'){
                *pch2=' ';
            }else if(*pch=='r'){
                *pch2='\r';
            }
            else if(*pch=='n'){
                *pch2='\n';
            }
            else if(*pch=='t'){
                *pch2='\t';
            }
            else if(*pch=='\\'){
                *pch2='\\';
            }else{
                *pch2='?';
            }
        }else{
            *pch2=*pch;
        }
        pch2++;
    }
    *pch2=0;
    free(buf2);
    dll=SDL_LoadObject(buf);
    free(buf);
    if(dll==NULL){
        printf("Entry Not Found");
        return 1;
    }
    entry_func=(SDL_main_func)SDL_LoadFunction(dll,"SDL_main");
    if(entry_func==NULL){
        printf("Entry Not Found");
        SDL_UnloadObject(dll);
        return 1;
    }
    returnFromSdl=entry_func(argc,argv);
    SDL_UnloadObject(dll);
    return returnFromSdl;
}