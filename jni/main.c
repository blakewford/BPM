/*      File main.c: 2.7 (84/11/28,10:14:56) */
/*% cc -O -c %
 *
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "defs.h"
#include "data.h"
#include "jni.h"
#include <android/log.h>
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "SmallC", __VA_ARGS__))

int runFlag = 0;

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM *vm, void *reserved)
{
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL 
Java_org_starlo_bytepusher_Compiler_compile(JNIEnv *env, jobject obj, jstring filePath)
{
    const char *nativeString = (*env)->GetStringUTFChars(env, filePath, NULL);
	main(2, &nativeString);
    (*env)->ReleaseStringUTFChars(env, filePath, nativeString);
}

JNIEXPORT void JNICALL 
Java_org_starlo_bytepusher_Compiler_deploy(JNIEnv *env, jobject obj)
{
    jclass objclass = (*env)->GetObjectClass(env, obj);
    jmethodID method = (*env)->GetMethodID(env, objclass, "MemWrite", "([I)V");
	unsigned short i = 0x0000;
	srand(time(NULL));
	jint values[0x10000] = {0};
	jintArray jvalues = (*env)->NewIntArray(env, 0x10000);
	runFlag = 1;
    while(runFlag){
		values[i] = 0x91;
		if (i == 0xFFFF) {
			(*env)->SetIntArrayRegion(env, jvalues, 0, 0x10000, values);
			(*env)->CallVoidMethod(env, obj, method, jvalues);
			sleep(1);
		}
		i++;
	}
}

JNIEXPORT void JNICALL 
Java_org_starlo_bytepusher_Compiler_stop(JNIEnv *env, jobject obj)
{
	runFlag = 0;
	sleep(2);
}

main(argc, argv)
int argc;
char *argv[];
{
    char *p = NULL, *bp;
    int smacptr;
    macptr = 0;
    ctext = 0;
    errs = 0;
    aflag = 1;
    int i=1;
    for (; i<argc; i++) {
        p = *argv;
        if (*p == '-') {
            while (*++p) {
                switch (*p) {
                    case 't': case 'T': // output c source as asm comment
                        ctext = 1;
                        break;
                    case 's': case 'S':
                        sflag = 1;
                        break;
                    case 'c': case 'C': 
                        cflag = 1;
                        break;
                    case 'a': case 'A':
                        aflag = 0;
                        break;
                    case 'd': case 'D': // define macro
                        bp = ++p;
                        if (!*p) usage();
                        while (*p && *p != '=') p++;
                        if (*p == '=') *p = '\t';
                        while (*p) p++;
                        p--;
                        defmac(bp);
                        break;
                    default:
                        usage();
                }
            }
        } else {
            break;
        }
    }
	
    smacptr = macptr;
    if (!p) {
        usage();
    }
	
    for (; i<argc; i++) {
        p = *argv;
        errfile = 0;
        if (filename_typeof(p) == 'c') {
            glbptr = STARTGLB;
            locptr = STARTLOC;
            wsptr = ws;
            inclsp =
            iflevel =
            skiplevel =
            swstp =
            litptr =
            stkp =
            errcnt =
            ncmp =
            lastst =
            quote[1] =
            0;
            macptr = smacptr;
            input2 = NULL;
            quote[0] = '"';
            cmode = 1;
            glbflag = 1;
            nxtlab = 0;
            litlab = getlabel();
            defmac("end\tmemory");
            addglb("memory", ARRAY, CCHAR, 0, EXTERN);
            addglb("stack", ARRAY, CCHAR, 0, EXTERN);
            rglbptr = glbptr;
            addglb("etext", ARRAY, CCHAR, 0, EXTERN);
            addglb("edata", ARRAY, CCHAR, 0, EXTERN);
            defmac("short\tint");
            initmac();
            /*
             *      compiler body
             */
            if (!openin(p))
                return;
            if (!openout())
                return;
            header();
            gtext();
            parse();
            fclose(input);
            gdata();
            dumplits();
            dumpglbs();
            errorsummary();
            trailer();
            fclose(output);
            pl("");
            errs = errs || errfile;
#ifndef NOASLD
        }
        if (!errfile && !sflag) {
            errs = errs || assemble(p);
        }
#else
        } else {
            fputs("Don't understand file ", stderr);
            fputs(p, stderr);
            errs = 1;
        }
#endif
    }
#ifndef NOASLD
    if (!errs && !sflag && !cflag)
        errs = errs || link();
#endif
    //exit(errs != 0);

}

FEvers() {
    outstr("\tFront End (2.7,84/11/28)");
}

/**
 * prints usage
 * @return exits the execution
 */
usage() {
    fputs("usage: sccXXXX [-tcsa] [-dSYM[=VALUE]] files\n", stderr);
    exit(1);
}

/**
 * process all input text
 *
 * at this level, only static declarations, defines, includes,
 * and function definitions are legal.
 *
 */
parse() {
    while (!feof(input)) {
        if (amatch("extern", 6))
            dodcls(EXTERN);
        else if (amatch("static", 6))
            dodcls(STATIC);
        else if (dodcls(PUBLIC));
        else if (match("#asm"))
            doasm();
        else if (match("#include"))
            doinclude();
        else if (match("#define"))
            dodefine();
        else if (match("#undef"))
            doundef();
        else
            newfunc();
        blanks();
    }
}

/**
 * parse top level declarations
 * @param stclass
 * @return 
 */
dodcls(stclass)
int stclass;
{
    blanks();
    if (amatch("char", 4))
        declglb(CCHAR, stclass);
    else if (amatch("int", 3))
        declglb(CINT, stclass);
    else if (stclass == PUBLIC)
        return (0);
    else
        declglb(CINT, stclass);
    ns();
    return (1);
}

/**
 * dump the literal pool
 */
dumplits() {
    int j, k;

    if (litptr == 0)
        return;
    printlabel(litlab);
    col();
    k = 0;
    while (k < litptr) {
        defbyte();
        j = 8;
        while (j--) {
            onum(litq[k++] & 127);
            if ((j == 0) | (k >= litptr)) {
                nl();
                break;
            }
            outbyte(',');
        }
    }
}

/**
 * dump all static variables
 */
dumpglbs() {
    int j;

    if (!glbflag)
        return;
    cptr = rglbptr;
    while (cptr < glbptr) {
        if (cptr[IDENT] != FUNCTION) {
            ppubext(cptr);
            if (cptr[STORAGE] != EXTERN) {
                prefix();
                outstr(cptr);
                col();
                defstorage();
                j = glint(cptr);
                if ((cptr[TYPE] == CINT) ||
                        (cptr[IDENT] == POINTER))
                    j = j * intsize();
                onum(j);
                nl();
            }
        } else {
            fpubext(cptr);
        }
        cptr = cptr + SYMSIZ;
    }
}

/*
 *      report errors
 */
errorsummary() {
    if (ncmp)
        error("missing closing bracket");
    nl();
    comment();
    outdec(errcnt);
    if (errcnt) errfile = YES;
    outstr(" error(s) in compilation");
    nl();
    comment();
    ot("literal pool:");
    outdec(litptr);
    nl();
    comment();
    ot("global pool:");
    outdec(glbptr - rglbptr);
    nl();
    comment();
    ot("Macro pool:");
    outdec(macptr);
    nl();
    pl(errcnt ? "Error(s)" : "No errors");
}

/**
 * test for C or similar filename, e.g. xxxxx.x, tests the dot at end-1 postion
 * @param s the filename
 * @return the last char if it contains dot, space otherwise
 */
filename_typeof(s)
char *s;
{
    s += strlen(s) - 2;
    if (*s == '.')
        return (*(s + 1));
    return (' ');
}
