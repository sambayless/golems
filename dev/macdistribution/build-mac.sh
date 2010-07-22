#!/bin/bash
export CXXFLAGS="-arch ppc -arch i386"
export CFLAGS="-mmacosx-version-min=10.4 -arch ppc -arch i386  -isysroot /Developer/SDKs/MacOSX10.4u.sdk"
export LDFLAGS=$CXXFLAGS



export JAVA_INCLUDE="/System/Library/Frameworks/JavaVM.framework/Headers"
echo Running ./configure
 ./configure --disable-dependency-tracking --disable-demos --with-trimesh=gimpact  --disable-debug;

#Note: have to change malloc.h to sys/malloc.h in gim_memory.cpp in gimpact

echo Running make for ODE
 make -s;
cd ../src
echo Compiling odejava natives

#note: dev_sdk is not defined...


 g++ $CFLAGS -fPIC -DHAVE_CONFIG_H -I../src/c -I../opende/include -I../opende/ode/src -O2 \
     -fno-strict-aliasing -fomit-frame-pointer -ffast-math -Iinclude -IOPCODE -IOPCODE/Ice \
     -I$JAVA_INCLUDE/osx  -I$JAVA_INCLUDE -DdTRIMESH_ENABLED -c -o ../bin/odejava_wrap.o \
     ../generated/c++/odejava.cxx

g++ $CFLAGS -fPIC -DHAVE_CONFIG_H -I../src/c -I../opende/include -I../opende/ode/src -O2 \
     -fno-strict-aliasing -fomit-frame-pointer -ffast-math -Iinclude -IOPCODE -IOPCODE/Ice \
     -I$JAVA_INCLUDE/osx -I$JAVA_INCLUDE -DdTRIMESH_ENABLED -c -o ../bin/odejava.o \
     ../src/c/odejava.cpp

echo Linking odejava and ODE into library
 g++ $CFLAGS -dynamiclib -fPIC `find ../opende/ode/src -name *.o` ../bin/odejava_wrap.o ../bin/odejava.o -o ../bin/libodejava.jnilib

#/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Headers
#g++ -arch ppc -arch i386 -isysroot /Developer/SDKs/MacOSX10.5.sdk -fPIC -DHAVE_CONFIG_H -I../src/c -I../opende/include -I../opende/ode/src -O2      -fno-strict-aliasing -fomit-frame-pointer -ffast-math -Iinclude -IOPCODE -IOPCODE/Ice    -I$JAVA_INCLUDE/osx  -I$JAVA_INCLUDE -DdTRIMESH_ENABLED -c -o ../bin/odejava_wrap.o      ../generated/c++/odejava.cxx


#instructions: go to the opende dir (with terminal) that is INSIDE the odejava folder that is INSIDE odejava/odejava-jni/build/odejava/odejava-jni/

#make clean

#define the flags below by manually calling them 
#export CXXFLAGS="-arch ppc -arch i386"
#export CFLAGS="-mmacosx-version-min=10.4 -arch ppc -arch i386  -isysroot /Developer/SDKs/MacOSX10.4u.sdk"
#export LDFLAGS=$CXXFLAGS

#export JAVA_INCLUDE="/System/Library/Frameworks/JavaVM.framework/Headers"
#call: 
#./configure --disable-dependency-tracking --disable-demos --with-trimesh=gimpact  --disable-debug;
#then call the three compilations, still from the opende folder

 #g++ $CFLAGS -fPIC -DHAVE_CONFIG_H -I../src/c -I../opende/include -I../opende/ode/src -O2 \
 #    -fno-strict-aliasing -fomit-frame-pointer -ffast-math -Iinclude -IOPCODE -IOPCODE/Ice \
 #    -I$JAVA_INCLUDE/osx  -I$JAVA_INCLUDE -DdTRIMESH_ENABLED -c -o ../bin/odejava_wrap.o \
 #    ../generated/c++/odejava.cxx

#g++ $CFLAGS -fPIC -DHAVE_CONFIG_H -I../src/c -I../opende/include -I../opende/ode/src -O2 \
#     -fno-strict-aliasing -fomit-frame-pointer -ffast-math -Iinclude -IOPCODE -IOPCODE/Ice \
#     -I$JAVA_INCLUDE/osx -I$JAVA_INCLUDE -DdTRIMESH_ENABLED -c -o ../bin/odejava.o \
#     ../src/c/odejava.cpp

#echo Linking odejava and ODE into library
# g++ $CFLAGS -dynamiclib -fPIC `find ../opende/ode/src -name *.o` ../bin/odejava_wrap.o ../bin/odejava.o #-o ../bin/libodejava.jnilib

#your lip is in odejava/odejava-jni/build/odejava/odejava-jni/bin