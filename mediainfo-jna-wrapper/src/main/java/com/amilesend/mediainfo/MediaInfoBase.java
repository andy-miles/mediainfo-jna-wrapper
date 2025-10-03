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
package com.amilesend.mediainfo;

import com.amilesend.mediainfo.lib.MediaInfoAccessor;
import com.amilesend.mediainfo.type.Status;
import com.amilesend.mediainfo.util.StringUtils;
import com.amilesend.mediainfo.util.Validate;
import com.amilesend.mediainfo.util.VisibleForTesting;
import com.sun.jna.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Base class for opening media files and accessing media information.
 *
 * Example usage:
 *
 * <pre>
 * // Extend to define which parameters to access for your java application
 * public class MyVideoMediaInfoType extends MediaInfoBase<MyVideoMediaInfoType> {
 *     public MyMediaInfoType(MediaInfoAccessor accessor) {
 *         super(accessor);
 *     }
 *
 *     public List<String> getVideoCodecs() {
 *         String codecsList = mediaInfo.get(StreamType.General, 0, "Video_Codec_List");
 *         return parseList(codecsList);
 *     }
 *
 *     // Define additional accessor methods here...
 * }
 *
 * MediaInfoLibrary library = MediaInfoLibrary.newInstance();
 * MediaInfoAccessor accessor = new MediaInfoAccessor(library);
 * try (MyVideoMediaInfoType myVideo = new MyVideoMediaInfoType(accessor).open("./MyVideo.mkv")) {
 *     List<String> videoCodecs = myVideo.getVideoCodecs();
 *
 *     // Access customized parameters accessor methods
 * }
 * </pre>
 *
 * @param <T> the concrete media info implementation type
 */
@Slf4j
@RequiredArgsConstructor
public abstract class MediaInfoBase<T extends MediaInfoBase> implements AutoCloseable {
    private static final int BUFFER_SIZE_4_MB = 4194304;
    private static final int MIN_FILE_SIZE = 65536;
    private static final int MAX_FILENAME_LENGTH_WIN = 250;
    private static final String LIST_DELIMITER = "/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("zzz uuuu-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_ALT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss zzz");

    private final ReentrantLock lock = new ReentrantLock();
    @Getter(AccessLevel.PROTECTED)
    private final MediaInfoAccessor accessor;

    /**
     * Opens a file for analysis to parse media information.
     *
     * @param file the media file
     * @return the analyzer to retrieve information
     * @throws IOException if an error occurred while opening the media file
     */
    public T open(@NonNull final File file) throws IOException {
        Validate.isTrue(file.isFile(), "File must be a file");
        Validate.isTrue(file.length() >= MIN_FILE_SIZE, "File size must be >= " + MIN_FILE_SIZE);

        lock.lock();
        try {
            final String filePath = file.getCanonicalPath();
            if (preferOpenViaBuffer(filePath)) {
                return readViaBuffer(file);
            }

            if (accessor.open(filePath) == true) {
                return (T) this;
            }

            throw new IOException("Failed to open media file: " + filePath);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            accessor.close();
        } finally {
            lock.unlock();
        }
    }

    @VisibleForTesting
    T readViaBuffer(final File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            if (openViaBuffer(raf)) {
                return (T) this;
            }

            throw new IOException("Failed to open media file buffer: " + file.getCanonicalPath());
        }
    }

    @VisibleForTesting
    boolean openViaBuffer(final RandomAccessFile file) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE_4_MB]; // Use 4MB buffer to reduce JNA calls
        if (accessor.openBufferInit(file.length(), 0) == false) {
            return false;
        }

        int read = -1;
        do {
            read = file.read(buffer);
            if (read < 0) {
                break;
            }

            final int result = accessor.openBufferContinue(buffer, read);
            if ((result & 8) == Status.Finalized.getValue()) {
                break;
            }

            final long gotoPos = accessor.openBufferContinueGotoGet();
            if (gotoPos >= Status.None.getValue()) {
                file.seek(gotoPos);
                accessor.openBufferInit(file.length(), gotoPos);
            }
        } while (read > 0);

        accessor.openBufferFinalize();
        return true;
    }

    @VisibleForTesting
    boolean preferOpenViaBuffer(final String path) {
        if (Platform.isWindows() && path.length() > MAX_FILENAME_LENGTH_WIN) {
            return true;
        }

        if (Platform.isMac() && !US_ASCII.newEncoder().canEncode(path)) {
            return true;
        }

        return false;
    }

    /**
     * Helper method used to parse timestamps.
     *
     * @param time the time as a String
     * @return the parsed Instant, or {@code null} if the time value is blank
     */
    public static Instant parseTime(final String time) {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        try {
            return ZonedDateTime.parse(time, DATE_TIME_FORMATTER).toInstant();
        } catch (final DateTimeParseException ex) {
            log.debug("Timestamp does not match pattern: \"zzz uuuu-MM-dd HH:mm:ss\"");
        }

        return ZonedDateTime.parse(time, DATE_TIME_FORMATTER_ALT).toInstant();
    }

    /**
     * Helper method used to parse a list of values.
     *
     * @param listValue the value with a list of items
     * @return the list of values
     */
    public static List<String> parseList(final String listValue) {
        if (StringUtils.isBlank(listValue)) {
            return Collections.emptyList();
        }

        return Arrays.stream(listValue.split(LIST_DELIMITER))
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
