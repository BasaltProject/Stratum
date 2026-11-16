# The Bukkit compatibility layer, which embeds Basalt Shim. Added when cmake/StratumBukkit.cmake finds a JDK.
set(STRATUM_JNI_INCLUDE_DIR "" CACHE PATH "Directory that holds jni.h (a JDK include directory)")
if(NOT STRATUM_JNI_INCLUDE_DIR)
    file(GLOB BASALT_JDK_INCLUDES /usr/lib/jvm/*/include /usr/local/openjdk*/include
         /Library/Java/JavaVirtualMachines/*/Contents/Home/include)
    find_path(STRATUM_JNI_INCLUDE_DIR_FOUND jni.h HINTS $ENV{JAVA_HOME}/include ${BASALT_JDK_INCLUDES}
              NO_CMAKE_FIND_ROOT_PATH)
    set(STRATUM_JNI_INCLUDE_DIR "${STRATUM_JNI_INCLUDE_DIR_FOUND}")
endif()
if(NOT STRATUM_JNI_INCLUDE_DIR)
    message(FATAL_ERROR "The Bukkit layer needs jni.h: set STRATUM_JNI_INCLUDE_DIR or JAVA_HOME")
endif()

basalt_module(stratum-bukkit
    SOURCES
        src/BukkitBridge.cpp
        src/BukkitEmbedded.cpp
        src/BukkitEntityNatives.cpp
        src/BukkitFileNatives.cpp
        src/BukkitHost.cpp
        src/BukkitItemNatives.cpp
        src/BukkitLeftoverNatives.cpp
        src/BukkitMechanicsNatives.cpp
        src/BukkitMiscNatives.cpp
        src/BukkitMiscDataNatives.cpp
        src/BukkitMiscBoardNatives.cpp
        src/BukkitMiscExtrasNatives.cpp
        src/BukkitMiscMapsNatives.cpp
        src/BukkitNatives.cpp
        src/BukkitPackets.cpp
        src/BukkitPermissionNatives.cpp
        src/BukkitPlayerNatives.cpp
        src/BukkitRestNatives.cpp
        src/BukkitWorldNatives.cpp
        src/BukkitWorldRegistryNatives.cpp
        src/BukkitWorldBlocksNatives.cpp
        src/BukkitWorldEntitiesNatives.cpp
        src/BukkitWorldChunksNatives.cpp
        src/BukkitWorldJobsNatives.cpp
        src/BukkitNpcNatives.cpp
        src/BukkitWorldEnvNatives.cpp
        src/BukkitWorldEnvMoreNatives.cpp
        src/ShimHost.cpp
    DEPENDS basalt::server-plugins basalt::wire-auth basalt::wire-common basalt::wire-io basalt::wire-nbt
            basalt::wire-protocol
)
target_include_directories(stratum-bukkit SYSTEM PUBLIC ${STRATUM_JNI_INCLUDE_DIR} ${STRATUM_JNI_INCLUDE_DIR}/linux
                                                      ${STRATUM_JNI_INCLUDE_DIR}/darwin ${STRATUM_JNI_INCLUDE_DIR}/win32
                                                      ${STRATUM_JNI_INCLUDE_DIR}/freebsd)
target_link_libraries(stratum-bukkit PUBLIC ${CMAKE_DL_LIBS})

file(MAKE_DIRECTORY ${BASALT_BUKKIT_EMBED_DIR})
set(BASALT_BUKKIT_EMBEDDED_FILES ${BASALT_BUKKIT_EMBED_DIR}/libjvm ${BASALT_BUKKIT_EMBED_DIR}/boot.jar
    ${BASALT_BUKKIT_EMBED_DIR}/runtime.jar ${BASALT_BUKKIT_EMBED_DIR}/libs.txt)
foreach(_file IN LISTS BASALT_BUKKIT_EMBEDDED_FILES)
    if(NOT EXISTS ${_file})
        file(WRITE ${_file} "")
    endif()
endforeach()
set_source_files_properties(src/BukkitEmbedded.cpp PROPERTIES OBJECT_DEPENDS "${BASALT_BUKKIT_EMBEDDED_FILES}"
                            COMPILE_DEFINITIONS "BASALT_BUKKIT_EMBED_DIR=\"${BASALT_BUKKIT_EMBED_DIR}\"")

if(BASALT_BUILD_TESTS)
    if(NOT CMAKE_CROSSCOMPILING)
        add_test(NAME stratum-bukkit-stage-runtime-test
            COMMAND ${CMAKE_COMMAND}
                "-DSTAGE_SCRIPT=${STRATUM_DIR}/cmake/StageBukkitRuntime.cmake"
                "-DTEST_ROOT=${CMAKE_CURRENT_BINARY_DIR}/stage-runtime-test"
                -P "${CMAKE_CURRENT_SOURCE_DIR}/test/StageRuntimeTest.cmake")
    endif()

    basalt_test(stratum-bukkit-shim-smoke-test
        SOURCES test/ShimSmokeTest.cpp
        DEPENDS basalt::stratum-bukkit basalt::wire-protocol
    )
    # Runners ship an older system javac that can shadow PATH lookups; prefer JAVA_HOME outright.
    # Do not preset BASALT_TEST_* to "": find_program skips searching when the variable is already set.
    foreach(_tool javac java)
        string(TOUPPER "${_tool}" _tool_var)
        set(_basalt_jdk_tool "")
        if(DEFINED ENV{JAVA_HOME})
            foreach(_candidate "$ENV{JAVA_HOME}/bin/${_tool}" "$ENV{JAVA_HOME}/bin/${_tool}.exe")
                if(EXISTS "${_candidate}")
                    set(_basalt_jdk_tool "${_candidate}")
                    break()
                endif()
            endforeach()
        endif()
        if(_basalt_jdk_tool)
            set(BASALT_TEST_${_tool_var} "${_basalt_jdk_tool}")
        else()
            find_program(BASALT_TEST_${_tool_var} ${_tool} HINTS $ENV{JAVA_HOME}/bin REQUIRED NO_CACHE NO_CMAKE_FIND_ROOT_PATH)
        endif()
    endforeach()
    set(BASALT_PATH_TEST_CLASSES "${CMAKE_CURRENT_BINARY_DIR}/path-test-classes")
    # A cross build's tests run on the target, which has no JDK
    if(NOT CMAKE_CROSSCOMPILING)
        add_test(NAME stratum-bukkit-path-translator-test
            COMMAND ${CMAKE_COMMAND}
                "-DJAVAC=${BASALT_TEST_JAVAC}" "-DJAVA=${BASALT_TEST_JAVA}"
                "-DSOURCE_DIR=${CMAKE_CURRENT_SOURCE_DIR}" "-DOUT=${BASALT_PATH_TEST_CLASSES}"
                -P "${CMAKE_CURRENT_SOURCE_DIR}/test/PathTranslatorTest.cmake")
    endif()
    set(BASALT_CP_SEP ":")
    if(WIN32)
        set(BASALT_CP_SEP "$<SEMICOLON>")
    endif()
    set(BASALT_COMPAT_TEST_CLASSES "${CMAKE_CURRENT_BINARY_DIR}/compat-test-classes")
    add_custom_target(stratum-bukkit-compat-test-classes
        COMMAND ${CMAKE_COMMAND} -E make_directory "${BASALT_COMPAT_TEST_CLASSES}"
        COMMAND "${BASALT_TEST_JAVAC}" --release 25 -proc:none
            -cp "${CMAKE_BINARY_DIR}/bukkit-runtime/basalt-bukkit.jar${BASALT_CP_SEP}${CMAKE_BINARY_DIR}/bukkit-runtime/lib/*"
            -d "${BASALT_COMPAT_TEST_CLASSES}" "${CMAKE_CURRENT_SOURCE_DIR}/test/java/RuntimeCompatibilityTest.java"
        COMMAND ${CMAKE_COMMAND} -E rm -f "${BASALT_COMPAT_TEST_CLASSES}/compatibility/MissingDependency.class"
        DEPENDS basalt-bukkit-runtime
        VERBATIM)
    add_dependencies(stratum-bukkit-shim-smoke-test stratum-bukkit-compat-test-classes)
    target_compile_definitions(stratum-bukkit-shim-smoke-test PRIVATE
        BASALT_TEST_SCRATCH_DIR="${CMAKE_CURRENT_BINARY_DIR}/shim-smoke-test"
        BASALT_TEST_RUNTIME_DIR="${CMAKE_BINARY_DIR}/bukkit-runtime"
        BASALT_COMPAT_TEST_CLASSES="${BASALT_COMPAT_TEST_CLASSES}")
    set_tests_properties(stratum-bukkit-shim-smoke-test PROPERTIES TIMEOUT 60 ENVIRONMENT "ASAN_OPTIONS=detect_leaks=0")
endif()
