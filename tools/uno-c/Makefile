#all: Implication.cpp Main.cpp SetOperations.cpp shd.h Table.h Implication.h Makefile SetOperations.h Table.cpp timestamp.cpp !!! oren ???
#	g++ -O3 -I../shd31 *.cpp ../shd31/shd.o -o DBasisHDA

#	g++ -Og -I../shd31 ^$ ../shd31/shd.o -o DBasisHDA # !!! !!! 10.28.17 oren change: compile for best debugging experience

#########################################
# !!! 11.10.17 oren -> improve make file
#########################################
# Notes: 
#   Use make DEBUG=0 for release version
#   Use make DEBUG=1 for debug version


DEL_CMD  := rm -r #f
MAKE_DIR := mkdir -p

BUILD_DIR:= .

ifeq ($(DEBUG), 1)
  $(info Make running in debug mode)
  OBJ_TYPE := Debug
  OBJ_DIR  := $(BUILD_DIR)/$(OBJ_TYPE)
  LDFLAGS  := -Loslc/Debug 
  LIBS     := -loslc
  CXXFLAGS := -std=c++11 -O0 -g3 -Wall -fmessage-length=0 -Wno-sign-compare
else
  $(info Make running in release mode)
  OBJ_TYPE := Release
  OBJ_DIR  := $(BUILD_DIR)/$(OBJ_TYPE)
  LDFLAGS  := -Loslc/Release 
  LIBS     := -loslc
  CXXFLAGS := -std=c++11 -O3 -g3 -Wall -fmessage-length=0 -Wno-sign-compare
endif


CXX      := g++
#CXXFLAGS := -std=c++11 -O0 -g3 -Wall -fmessage-length=0 -Wno-sign-compare
#LDFLAGS  := #-L/usr/lib 
#OBJ_DIR  := $(BUILD_DIR)/Debug
APP_DIR  := $(OBJ_DIR)
TARGET   := DBasisHDA
INCLUDE  := -I"./shd31" -I"./oslc/src"
SRCS     :=                      \
   $(wildcard src/*.cpp) #\
#   $(wildcard ...) \

OBJS := $(SRCS:%.cpp=$(OBJ_DIR)/%.o) shd31/shd.o 	

#handle dependencies
DEPS := $(OBJS:%.o=%.d)
DEP_FLAGS:= -MMD -MP

$(OBJ_DIR)/%.o: %.cpp
	$(MAKE_DIR) $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) $(DEP_FLAGS) -o $@ -c $<

$(APP_DIR)/$(TARGET): $(OBJS)
	$(MAKE_DIR) $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) $(LDFLAGS) -o $(APP_DIR)/$(TARGET) $^ $(LIBS)

shd31/shd.o: shd31/shd.c
	$(CXX) $(CXXFLAGS) $(INCLUDE) -o $@ -c $<

#################################
# build library dependencies
#oslc/Debug/oslc.a: 
#	cd oslc && $(MAKE) lib
oslcLib: oslc
	$(MAKE) -C $< lib
#################################

all: oslcLib $(APP_DIR)/$(TARGET)

#ifneq("$(wildcard $(APP_DIR))","")
#ifneq ("$(OBJ_DIR)","$(APP_DIR)")
clean:
ifneq ("$(OBJ_DIR)","$(APP_DIR)")
	$(DEL_CMD) $(OBJ_DIR)
	$(DEL_CMD) $(APP_DIR)
else
#clean:
	$(DEL_CMD) $(OBJ_DIR)
endif

.PHONY: all clean oslcLib 

-include $(DEPS)


