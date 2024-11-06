/*
 * The MIT License
 * Copyright © 2024 Andy Miles
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.amilesend.mediainfo.lib;

import com.google.common.collect.ImmutableMap;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

import java.util.Collections;
import java.util.Map;

/** Defines the JNA interface to access the native libmediainfo library. */
public interface MediaInfoLibrary extends Library {
    String MEDIA_INFO_LIB_PATH = "mediainfo";

    /** Defines the java methods to library method mapping. */
    Map<String, String> METHOD_TO_FUNCTION_NAME_MAP = ImmutableMap.<String, String>builder()
            .put("newHandle", "MediaInfo_New")
            .put("deleteHandle", "MediaInfo_Delete")
            .put("open", "MediaInfo_Open")
            .put("openBufferInit", "MediaInfo_Open_Buffer_Init")
            .put("openBufferContinue", "MediaInfo_Open_Buffer_Continue")
            .put("openBufferContinueGotoGet", "MediaInfo_Open_Buffer_Continue_GoTo_Get")
            .put("openBufferFinalize", "MediaInfo_Open_Buffer_Finalize")
            .put("close", "MediaInfo_Close")
            .put("inform", "MediaInfo_Inform")
            .put("get", "MediaInfo_Get")
            .put("getI", "MediaInfo_GetI")
            .put("countGet", "MediaInfo_Count_Get")
            .put("option", "MediaInfo_Option")
            .build();

    /**
     * Creates a new {@link MediaInfoLibrary} instance.
     *
     * @return the media info library instance
     */
     static MediaInfoLibrary newInstance() {
         return Native.load(
                MEDIA_INFO_LIB_PATH,
                MediaInfoLibrary.class,
                Collections.singletonMap(
                        OPTION_FUNCTION_MAPPER,
                        (FunctionMapper) (lib, method) -> METHOD_TO_FUNCTION_NAME_MAP.get(method.getName())));
    }

    /////////
    // ctor
    /////////

    /**
     * Creates a new pointer reference to the libMediaInfo library.
     *
     * @return the pointer reference
     */
    Pointer newHandle();

    /**
     * Deallocates a pointer reference to the libMediaInfo library.
     *
     * @param handle the pointer reference
     */
    void deleteHandle(Pointer handle);

    ////////////////
    // File Access
    ////////////////

    /**
     * Opens a file for parsing media information and tags.
     *
     * @param handle the library pointer
     * @param file the full path to the file to open
     * @return {@code 1} if the file was successfully opened; else, {@code 0}
     */
    int open(Pointer handle, WString file);

    /**
     * Prepares a memory buffer for reading and parsing media information from a stream.
     *
     * @param length the length of the buffer
     * @param offset the byte offset to start reading from
     * @return {@code 1} if the buffer was successfully allocated; else, {@code 0}
     */
    int openBufferInit(Pointer handle, long length, long offset);

    /**
     * Reads from a memory buffer to parse media information and tags.
     *
     * @param handle the library pointer
     * @param buffer the buffer reference
     * @param size the amount of data to read
     * @return a bitfield with the following bits:
     *         <ul>
     *             <li>{@code 0} - Accepted (format is known)</li>
     *             <li>{@code 1} - Filled (data collected)</li>
     *             <li>{@code 2} - Buffer updated (further data required)</li>
     *             <li>{@code 3} - Buffer finalized (no further data required)</li>
     *             <li>{@code 4-15} - Reserved</li>
     *             <li>{@code 16-31} - User defined</li>
     *         </ul>
     */
    int openBufferContinue(Pointer handle, byte[] buffer, int size);

    /**
     * Tests if there is request to seek to another position in the stream.
     *
     * @param handle the library pointer
     * @return {@code -1} if there is no more data to seek to; else, the seek position
     */
    long openBufferContinueGotoGet(Pointer handle);

    /**
     * Closes the buffer upon read completion.
     *
     * @param handle the library pointer.
     * @return {@code 0} if the buffer has been read or null; else non-0 value if an error occurred.
     */
    int openBufferFinalize(Pointer handle);

    /**
     * Closes the file that was opened with {@link #open(Pointer, WString)}.
     *
     * @param handle the library pointer
     */
    void close(Pointer handle);

    //////////////////////
    // Media Information
    //////////////////////

    /**
     * Gets all media details about a file.
     *
     * @param handle the library pointer
     * @param reserved reserved value. Should be defined as {@code 0}
     * @return the media information
     */
    WString inform(Pointer handle, int reserved);

    /**
     * Gets information about a media file where the parameter is a string. For a list of available parameters, please
     * refer to <a href="https://github.com/MediaArea/MediaInfoLib/blob/master/Source/Resource/Text/Stream/General.csv">
     * General.csv</a>.
     *
     * @param handle the library pointer
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter
     * @param infoType the information type
     * @param searchType the search type
     * @return the media parameter information
     */
    WString get(Pointer handle, int streamType, int streamNumber, WString parameter, int infoType, int searchType);

    /**
     * Gets information about a media file where the parameter is an integer that represents the parameter index.
     *
     * @param handle the library pointer
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameterIndex the index position for the parameter
     * @param infoType the information type
     * @return the media parameter information
     */
    WString getI(Pointer handle, int streamType, int streamNumber, int parameterIndex, int infoType);

    /**
     * The count of streams for the given stream type, or count of information parameters for a stream.
     *
     * @param handle the library pointer
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @return the count of streams or parameters
     */
    int countGet(Pointer handle, int streamType, int streamNumber);

    ////////////
    // Options
    ////////////

    /**
     * Configure or gets information about MediaInfo
     *
     * @param handle the library pointer
     * @param option the option
     * @param value the value
     * @return {@code ""} means no; else, any other value means yes
     */
    WString option(Pointer handle, WString option, WString value);
}
