#ifndef FIO_JNI_H
#define FIO_JNI_H

#define CLASS_NAME_RAPTOR_MARK "io/github/devriesl/raptormark/RaptorActivity"

int fio(int argc, char *argv[], char *envp[]);

int read_to_pipe_async(int argc, char *argv[]);

#endif // FIO_JNI_H