/*
 * The MIT License
 * Copyright © 2024-2025 Andy Miles
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

import com.amilesend.mediainfo.type.InfoType;
import com.amilesend.mediainfo.type.Status;
import com.amilesend.mediainfo.type.StreamType;
import com.google.common.annotations.VisibleForTesting;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/** The object used by java applications to interact with the libMediaInfo library. */
@Slf4j
public class MediaInfoAccessor implements AutoCloseable {
    private final ReentrantLock lock = new ReentrantLock();
    private MediaInfoLibrary mediaInfoLibrary;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @VisibleForTesting
    private Pointer mediaInfoLibPointer;

    /**
     * Creates a new {@code MediaInfo} object.
     *
     * @param mediaInfoLibrary the library instance
     * @see MediaInfoLibrary
     */
    public MediaInfoAccessor(@NonNull final MediaInfoLibrary mediaInfoLibrary) {
        this.mediaInfoLibrary = mediaInfoLibrary;
        mediaInfoLibPointer = newPointer();
    }

    /** Closes the library instance. */
    @Override
    public void close() {
        dispose();
    }

    /** Disposes of the library instance reference. */
    public void dispose() {
        if (Objects.isNull(mediaInfoLibPointer)) {
            return;
        }

        lock.lock();
        try {
            mediaInfoLibrary.close(mediaInfoLibPointer);
            mediaInfoLibrary.deleteHandle(mediaInfoLibPointer);
            mediaInfoLibPointer = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Opens a file to parse.
     *
     * @param fileName the full path and filename to open
     * @return {@code true} if file was opened; else, {@code false}
     */
    public boolean open(final String fileName) {
        Validate.notBlank(fileName, "fileName must not be blank");

        lock.lock();
        try {
            if (Objects.isNull(mediaInfoLibPointer)) {
                mediaInfoLibPointer = newPointer();
            }

            int response = mediaInfoLibrary.open(mediaInfoLibPointer, new WString(fileName));
            return response == Status.Accepted.getValue();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Prepares a memory buffer for reading and parsing media information from a stream.
     *
     * @param length the length of the buffer
     * @param offset the byte offset to start reading from
     * @return {@code true} if the buffer was successfully initialized; else, {@code false}
     */
    public boolean openBufferInit(final long length, final long offset) {
        Validate.isTrue(length > 0L, "length must be > 0");
        Validate.isTrue(offset >= 0L, "offset must be >= 0");

        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking openBufferInit()");
        }

        lock.lock();
        try {
            int response = mediaInfoLibrary.openBufferInit(mediaInfoLibPointer, length, offset);
            return response == Status.Accepted.getValue();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reads from a memory buffer to parse media information and tags.
     *
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
    public int openBufferContinue(@NonNull final byte[] buffer, final int size) {
        Validate.isTrue(size > 0, "size must be > 0");

        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking openBufferContinue()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.openBufferContinue(mediaInfoLibPointer, buffer, size);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Tests if there is request to seek to another position in the stream.
     *
     * @return {@code -1} if there is no more data to seek to; else, the seek position
     */
    public long openBufferContinueGotoGet() {
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking openBufferContinueGotoGet()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.openBufferContinueGotoGet(mediaInfoLibPointer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Closes the buffer upon read completion.
     *
     * @return {@code 0} if the buffer has been read or null; else non-0 value if an error occurred.
     */
    public int openBufferFinalize() {
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking openBufferFinalize()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.openBufferFinalize(mediaInfoLibPointer);
        } finally {
            lock.unlock();
        }
    }

    /** Closes a file handle that was previously opened. */
    public void closeHandle() {
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking closeHandle()");
        }

        lock.lock();
        try {
            mediaInfoLibrary.close(mediaInfoLibPointer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get all details about a file.
     *
     * @return All details about a file in one string
     */
    public String inform() {
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking inform()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.inform(mediaInfoLibPointer, 0).toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get a piece of information about a file (parameter is a string).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @return the query result, or empty if there is a problem or not found
     * @see StreamType
     */
    public String get(final StreamType streamType, final int streamNumber, final String parameter) {
        return get(streamType, streamNumber, parameter, InfoType.Text, InfoType.Name);
    }


    /**
     * Get a piece of information about a file (parameter is a string).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @param infoType the type of information about the parameter
     * @see StreamType
     * @see InfoType
     */
    public String get(
            final StreamType streamType,
            final int streamNumber,
            final String parameter,
            final InfoType infoType) {
        return get(streamType, streamNumber, parameter, infoType, InfoType.Name);
    }

    /**
     * Get a piece of information about a file (parameter is a string). For a list of available parameters, please
     * refer to <a href="https://github.com/MediaArea/MediaInfoLib/blob/master/Source/Resource/Text/Stream/General.csv">
     * General.csv</a>.
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @param infoType the type of information about the parameter
     * @param searchType describes where to look for the parameter
     * @return the queries information; or an empty string if there was a problem
     */
    public String get(
            @NonNull final StreamType streamType,
            final int streamNumber,
            final String parameter,
            @NonNull final InfoType infoType,
            @NonNull final InfoType searchType) {
        Validate.isTrue(streamNumber >= 0, "streamNumber must be >= 0");
        Validate.notBlank(parameter, "parameter must not be blank");
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking get()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.get(
                    mediaInfoLibPointer,
                    streamType.ordinal(),
                    streamNumber,
                    new WString(parameter),
                    infoType.ordinal(),
                    searchType.ordinal()).toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get a piece of information about a file (parameter is an integer that represents the parameter index).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameterIndex the parameter index that you are looking for in the stream
     * @return a string about information you search, an empty string if there is a problem
     */
    public String get(final StreamType streamType, final int streamNumber, final int parameterIndex) {
        return get(streamType, streamNumber, parameterIndex, InfoType.Text);
    }

    /**
     * Get a piece of information about a file (parameter is an integer that represents the parameter index).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameterIndex the parameter index that you are looking for in the stream
     * @param infoType the type of information you want about the parameter (the text, the measure, the help...)
     * @return the information or an empty string if there is a problem
     */
    public String get(
            @NonNull final StreamType streamType,
            final int streamNumber,
            final int parameterIndex,
            @NonNull final InfoType infoType) {
        Validate.isTrue(streamNumber >= 0, "streamNumber must be > 0");
        Validate.isTrue(parameterIndex >= 0, "parameterIndex must be >= 0");
        if (Objects.isNull(mediaInfoLibPointer)) {
            throw new IllegalStateException("MediaInfoLib Pointer is null. This happens when close()/dispose() has " +
                    "been invoked prior to invoking get()");
        }

        lock.lock();
        try {
            return mediaInfoLibrary.getI(
                    mediaInfoLibPointer,
                    streamType.ordinal(),
                    streamNumber,
                    parameterIndex,
                    infoType.ordinal()).toString();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets the number of streams for the given stream type.
     *
     * @param streamType the stream type
     * @return number of streams of the given stream type
     */
    public int getStreamCount(@NonNull final StreamType streamType) {
        final String streamCount = get(streamType, 0, "StreamCount");
        if (StringUtils.isEmpty(streamCount)) {
            return 0;
        }

        return Integer.parseInt(streamCount);
    }


    /**
     * Gets the number of streams for the given stream type or the total count of information parameters for a stream.
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @return number of streams of the given stream type
     */
    public int getStreamOrParameterCount(@NonNull final StreamType streamType, final int streamNumber) {
        Validate.isTrue(streamNumber >= 0, "streamNumber must be > 0");

        lock.lock();
        try {
            return mediaInfoLibrary.countGet(mediaInfoLibPointer, streamType.ordinal(), streamNumber);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets information about MediaInfo.
     *
     * @param option The name of option
     * @return the option value
     */
    public String getOption(final String option) {
        return setOption(option, StringUtils.EMPTY);
    }

    /**
     * Configures information about MediaInfo.
     *
     * @param option The name of option
     * @param value The value of option
     * @return {@code ""} means no; else, any other value means yes
     */
    public String setOption(final String option, @NonNull final String value) {
        Validate.notBlank(option, "option must not be blank");

        lock.lock();
        try {
            return mediaInfoLibrary.option(mediaInfoLibPointer, new WString(option), new WString(value)).toString();
        } finally {
            lock.unlock();
        }
    }

    private Pointer newPointer() {
        try {
            return mediaInfoLibrary.newHandle();
        } catch(final LinkageError error) {
            throw new MediaInfoException(error);
        }
    }
}
