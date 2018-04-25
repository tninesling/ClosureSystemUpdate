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
  OBJ_DIR  := $(BUILD_DIR)/Debug
  CXXFLAGS := -std=c++11 -O0 -g3 -Wall -fmessage-length=0 -Wno-sign-compare
else
  $(info Make running in release mode)
  OBJ_DIR  := $(BUILD_DIR)/Release
  CXXFLAGS := -std=c++11 -O3 -g3 -Wall -fmessage-length=0 -Wno-sign-compare
endif


CXX      := g++
LDFLAGS  := #-L/usr/lib 
APP_DIR  := $(OBJ_DIR)
EXE_TARGET  := oslc
SLIB_TARGET := liboslc.a
INCLUDE  := -I"./src"
SRCS     :=                      \
   $(wildcard src/oslc/lul/*.cpp) #\
#   $(wildcard ...) \

OBJS := $(SRCS:%.cpp=$(OBJ_DIR)/%.o) 

#handle dependencies
DEPS := $(OBJS:%.o=%.d)
DEP_FLAGS:= -MMD -MP

$(OBJ_DIR)/%.o: %.cpp
	$(MAKE_DIR) $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) $(DEP_FLAGS) -o $@ -c $<

$(APP_DIR)/$(EXE_TARGET): $(OBJS)
	$(MAKE_DIR) $(@D)
	$(CXX) $(CXXFLAGS) $(INCLUDE) $(LDFLAGS) -o $(APP_DIR)/$(EXE_TARGET) $^
	
$(APP_DIR)/$(SLIB_TARGET): $(OBJS)
	$(MAKE_DIR) $(@D)
	ar rcs $@ $^	

exe: CXXFLAGS += -DLIB_EXE_MODE
exe: $(APP_DIR)/$(EXE_TARGET)

lib: $(APP_DIR)/$(SLIB_TARGET)
 
all: lib exe 

clean:
ifneq ("$(OBJ_DIR)","$(APP_DIR)")
	$(DEL_CMD) $(OBJ_DIR)
	$(DEL_CMD) $(APP_DIR)
else
	$(DEL_CMD) $(OBJ_DIR)
endif

.PHONY: all clean exe lib

-include $(DEPS)


