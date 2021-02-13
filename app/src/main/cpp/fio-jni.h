#ifndef FIO_JNI_H
#define FIO_JNI_H

#define CLASS_NAME_RAPTOR_MARK "io/github/devriesl/raptormark/data/TestBaseJNI"

#define MAX_ENGINE_NUM 32

#ifdef __cplusplus
extern "C" {
#endif

int fio(int argc, char *argv[], char *envp[]);

int read_to_pipe_async(int argc, char *argv[]);

int fio_list_ioengines(char **list_buf);

#ifdef __cplusplus
}
#endif

#endif // FIO_JNI_H