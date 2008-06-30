#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_INIT(libantlr3c, 3.0.1, jimi@idle.ws)
AC_PREREQ(2.60)
AC_COPYRIGHT([
 (The "BSD licence")
 Copyright (c) 2005-2006 Terence Parr
 ANTLR C templates and runtime Copyright (c) 2005-2006 Jim Idle
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
])
AC_CONFIG_SRCDIR(src/antlr3parser.c)


AC_ARG_ENABLE([debuginfo],
		[AS_HELP_STRING([--enable-debuginfo], [Compiles debug info into the library (default no)])],
		 [DEBUGINF=1], [DEBUGINF=0])

AC_ARG_ENABLE([64bit],
		[AS_HELP_STRING([--enable-64bit], [Turns on flags that produce 64 bit object code if any are required (default no)])],
		[BIT64=1], [BIT64=0])



AM_INIT_AUTOMAKE(foreign)
AC_LANG(C)
AC_PROG_CC([xlc aCC gcc cc])
AM_MAINTAINER_MODE
AM_PROG_LIBTOOL

AC_CANONICAL_BUILD
AC_CANONICAL_HOST

OBJECT_MODE=
# Checks for programs.
AC_MSG_CHECKING([compiler flags required for compiling ANTLR with $CC C compiler on host $host])

case $CC in
xlc*)
 CPPCMNTFLAGS="-qcpluscmt"
 if test "$BIT64" = 1; then
  CCFLAGS64="-q64 -Wl,-b64"
  OBJECT_MODE="64"
 else
  OBJECT_MODE="32"
 fi
 OPTIMFLAGS="-O2"
 if test "$DEBUGINF" = 1; then
  DEBUGFLAGS="-g"
 fi
 ;;

aCC*)
 CPPCMNTFLAGS=
 if test "$BIT64" = 1; then
  CCFLAGS64="+DD64"
 fi
 OPTIMFLAGS="-O"
 if test $DEBUGINF = 1; then
  DEBUGFLAGS="-g"
 fi
 ;;

gcc*)
 CPPCMNTFLAGS=
 CCFLAGS64=
 OPTIMFLAGS="-O2"
 if test $DEBUGINF = 1; then
  DEBUGFLAGS="-g"
 fi
 ;;

*)

case $host in
sparc*-*solaris*)
 CPPCMNTFLAGS=
 if test "$BIT64" = 1; then
  CCFLAGS64="-fast -xtarget=ultra4 -xarch=v9a"
 fi
 OPTIMFLAGS="-O"
 if test $DEBUGINF = 1; then
  DEBUGFLAGS='-g'
 fi
 ;;

*)
 CPPCMNTFLAGS=
 CCFLAGS64=
 OPTIMFLAGS="-O"
 if test $DEBUGINF = 1; then
  DEBUGFLAGS='-g'
 fi
 ;;
esac

 ;;
esac

CFLAGS="$CCFLAGS64 $CPPCMNTFLAGS $OPTIMFLAGS $DEBUGFLAGS"
AC_MSG_RESULT([$CFLAGS])
AC_SUBST([OBJECT_MODE])



AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_MAKE_SET

# Checks for libraries.

# Checks for header files.
AC_INCLUDES_DEFAULT()
AC_CHECK_HEADERS([malloc.h sys/malloc.h stdarg.h sys/stat.h ctype.h])

# Checks for typedefs, structures, and compiler characteristics.
AC_C_CONST
AC_TYPE_SIZE_T
AC_TYPE_INT8_T
AC_TYPE_INT16_T
AC_TYPE_INT32_T
AC_TYPE_INT64_T
AC_TYPE_INTPTR_T
AC_TYPE_UINT8_T
AC_TYPE_UINT16_T
AC_TYPE_UINT32_T
AC_TYPE_UINT64_T
AC_TYPE_UINTPTR_T
AC_C_INLINE


# Checks for library functions.
AC_CHECK_FUNCS([memmove memset strdup])

AC_CONFIG_HEADERS(antlr3config.h)
AC_CONFIG_FILES([Makefile])
AC_OUTPUT
