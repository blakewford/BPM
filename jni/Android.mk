LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := sccas09
LOCAL_SRC_FILES := code8080.c\
		error.c\
		function.c\
		io.c\
		main.c\
		primary.c\
		data.c\
		expr.c\
		gen.c\
		lex.c\
		preproc.c\
		stmt.c\
		sym.c\
		while.c

LOCAL_LDLIBS    := -llog -landroid

include $(BUILD_SHARED_LIBRARY)
