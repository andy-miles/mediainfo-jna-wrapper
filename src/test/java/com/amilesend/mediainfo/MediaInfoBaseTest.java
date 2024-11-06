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
package com.amilesend.mediainfo;

import com.amilesend.mediainfo.lib.MediaInfoAccessor;
import com.amilesend.mediainfo.type.Status;
import com.sun.jna.Platform;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MediaInfoBaseTest {
    @Mock
    private MediaInfoAccessor mockAccessor;
    private MyTestVideoInfo mediaInfoUnderTest;

    @BeforeEach
    public void setUp() {
        mediaInfoUnderTest = spy(new MyTestVideoInfo(mockAccessor));
    }

    @SneakyThrows
    public File setUpMockFile() {
        final File mockFile = mock(File.class);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.length()).thenReturn(4194304L);
        when(mockFile.getCanonicalPath()).thenReturn("/full/path/to/file.mkv");

        return mockFile;
    }

    /////////
    // open
    /////////

    @Test
    @SneakyThrows
    public void open_withReadViaBuffer_shouldReturnMediaInfo() {
        final File mockFile = setUpMockFile();
        doReturn(true).when(mediaInfoUnderTest).preferOpenViaBuffer(anyString());
        doReturn(mediaInfoUnderTest).when(mediaInfoUnderTest).readViaBuffer(any(File.class));

        final MyTestVideoInfo actual = mediaInfoUnderTest.open(mockFile);

        assertAll(
                () -> assertEquals(mediaInfoUnderTest, actual),
                () -> verify(mediaInfoUnderTest).preferOpenViaBuffer(isA(String.class)),
                () -> verifyNoInteractions(mockAccessor));
    }

    @Test
    @SneakyThrows
    public void open_withoutBuffer_shouldReturnMediaInfo() {
        final File mockFile = setUpMockFile();
        doReturn(false).when(mediaInfoUnderTest).preferOpenViaBuffer(anyString());
        when(mockAccessor.open(anyString())).thenReturn(true);

        final MyTestVideoInfo actual = mediaInfoUnderTest.open(mockFile);

        assertAll(
                () -> assertEquals(mediaInfoUnderTest, actual),
                () -> verify(mediaInfoUnderTest).preferOpenViaBuffer(isA(String.class)),
                () -> verify(mockAccessor).open(isA(String.class)));
    }

    @Test
    @SneakyThrows
    public void open_withOpenFailing_shouldThrowException() {
        final File mockFile = setUpMockFile();
        doReturn(false).when(mediaInfoUnderTest).preferOpenViaBuffer(anyString());
        when(mockAccessor.open(anyString())).thenReturn(false);

        assertThrows(IOException.class, () -> mediaInfoUnderTest.open(mockFile));
    }

    @Test
    public void open_withNullFile_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> mediaInfoUnderTest.open(null));
    }

    @Test
    public void open_withTooSmallFileSize_shouldThrowException() {
        final File mockFile = mock(File.class);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.length()).thenReturn(1L);

        assertThrows(IllegalArgumentException.class, () -> mediaInfoUnderTest.open(mockFile));
    }

    @Test
    public void open_withDirectory_shouldThrowExecption() {
        final File mockFile = mock(File.class);
        when(mockFile.isFile()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> mediaInfoUnderTest.open(mockFile));
    }

    //////////
    // close
    //////////

    @Test
    public void close_shouldCloseAccessor() {
        mediaInfoUnderTest.close();

        verify(mockAccessor).close();
    }

    //////////////////
    // readViaBuffer
    //////////////////

    @Test
    @SneakyThrows
    public void readViaBuffer_withFileOpened_shouldReturnMediaInfo() {
        final File mockFile = mock(File.class);
        doReturn(true).when(mediaInfoUnderTest).openViaBuffer(any(RandomAccessFile.class));

        try (MockedConstruction<RandomAccessFile> rafCons = mockConstruction(RandomAccessFile.class)) {
            final MyTestVideoInfo actual = mediaInfoUnderTest.readViaBuffer(mockFile);

            assertEquals(mediaInfoUnderTest, actual);
        }
    }

    @Test
    @SneakyThrows
    public void readViaBuffer_withFailedFileOpen_shouldThrowException() {
        final File mockFile = mock(File.class);
        when(mockFile.getCanonicalPath()).thenReturn("/path/file.mkv");
        doReturn(false).when(mediaInfoUnderTest).openViaBuffer(any(RandomAccessFile.class));

        try (MockedConstruction<RandomAccessFile> rafCons = mockConstruction(RandomAccessFile.class)) {
            assertThrows(IOException.class, () -> mediaInfoUnderTest.readViaBuffer(mockFile));
        }
    }

    //////////////////
    // openViaBuffer
    //////////////////

    @Test
    @SneakyThrows
    public void openViaBuffer_withBufferAllocated_shouldReadFile() {
        final RandomAccessFile mockFile = mock(RandomAccessFile.class);
        when(mockFile.read(any(byte[].class))).thenReturn(0);
        when(mockFile.length()).thenReturn(512L);
        when(mockAccessor.openBufferInit(anyLong(), anyLong())).thenReturn(true);
        when(mockAccessor.openBufferContinue(any(byte[].class), anyInt())).thenReturn(Status.Updated.getValue());
        when(mockAccessor.openBufferContinueGotoGet()).thenReturn(1L);
        when(mockAccessor.openBufferFinalize()).thenReturn(0);

        assertTrue(mediaInfoUnderTest.openViaBuffer(mockFile));
    }

    @Test
    @SneakyThrows
    public void openViaBuffer_withBufferAllocatedAndNoGotoPos_shouldReadFile() {
        final RandomAccessFile mockFile = mock(RandomAccessFile.class);
        when(mockFile.read(any(byte[].class))).thenReturn(0);
        when(mockFile.length()).thenReturn(512L);
        when(mockAccessor.openBufferInit(anyLong(), anyLong())).thenReturn(true);
        when(mockAccessor.openBufferContinue(any(byte[].class), anyInt())).thenReturn(Status.Updated.getValue());
        when(mockAccessor.openBufferContinueGotoGet()).thenReturn(-1L);

        final boolean actual = mediaInfoUnderTest.openViaBuffer(mockFile);

        assertAll(
                () -> assertTrue(actual),
                () -> verify(mockAccessor, never()).openBufferInit(eq(512L), eq(-1L)));
    }

    @Test
    @SneakyThrows
    public void openViaBuffer_withBufferAllocatedAndFinalized_shouldReadFile() {
        final RandomAccessFile mockFile = mock(RandomAccessFile.class);
        when(mockFile.read(any(byte[].class))).thenReturn(0);
        when(mockAccessor.openBufferInit(anyLong(), anyLong())).thenReturn(true);
        when(mockAccessor.openBufferContinue(any(byte[].class), anyInt())).thenReturn(8);

        final boolean actual = mediaInfoUnderTest.openViaBuffer(mockFile);

        assertAll(
                () -> assertTrue(actual),
                () -> verify(mockAccessor, never()).openBufferContinueGotoGet());
    }

    @Test
    @SneakyThrows
    public void openViaBuffer_withBufferInitFailed_shouldNotReadFile() {
        final RandomAccessFile mockFile = mock(RandomAccessFile.class);
        when(mockAccessor.openBufferInit(anyLong(), anyLong())).thenReturn(false);

        final boolean actual = mediaInfoUnderTest.openViaBuffer(mockFile);

        assertAll(
                () -> assertFalse(actual),
                () -> verify(mockAccessor, never()).openBufferContinue(any(byte[].class), anyInt()),
                () -> verify(mockAccessor, never()).openBufferFinalize());
    }

    @Test
    @SneakyThrows
    public void openViaBuffer_withIOException_shouldThrowException() {
        final RandomAccessFile mockFile = mock(RandomAccessFile.class);
        when(mockFile.read(any(byte[].class))).thenThrow(new IOException("Exception"));
        when(mockAccessor.openBufferInit(anyLong(), anyLong())).thenReturn(true);

        assertThrows(IOException.class, () -> mediaInfoUnderTest.openViaBuffer(mockFile));
    }

    ////////////////////////
    // preferOpenViaBuffer
    ////////////////////////

    @Test
    public void preferOpenViaBuffer_withWindowsAndLargeFilename_shouldReturnTrue() {
        try (MockedStatic<Platform> platformMockedStatic = mockStatic(Platform.class)) {
            platformMockedStatic.when(() -> Platform.isWindows()).thenReturn(true);

            assertTrue(mediaInfoUnderTest.preferOpenViaBuffer(RandomStringUtils.random(256)));
        }
    }

    @Test
    public void preferOpenViaBuffer_withWindowsAndSmallFilename_shouldReturnFalse() {
        try (MockedStatic<Platform> platformMockedStatic = mockStatic(Platform.class)) {
            platformMockedStatic.when(() -> Platform.isWindows()).thenReturn(true);

            assertFalse(mediaInfoUnderTest.preferOpenViaBuffer(RandomStringUtils.random(100)));
        }
    }

    @Test
    public void preferOpenViaBuffer_withMacAndNonAsciiEncodedPath_shouldReturnTrue() {
        try (MockedStatic<Platform> platformMockedStatic = mockStatic(Platform.class)) {
            platformMockedStatic.when(() -> Platform.isWindows()).thenReturn(false);
            platformMockedStatic.when(() -> Platform.isMac()).thenReturn(true);

            assertTrue(mediaInfoUnderTest.preferOpenViaBuffer("ÃnotWorkingFilename"));
        }
    }

    @Test
    public void preferOpenViaBuffer_withMacAndAsciiEncodedPath_shouldReturnFalse() {
        try (MockedStatic<Platform> platformMockedStatic = mockStatic(Platform.class)) {
            platformMockedStatic.when(() -> Platform.isWindows()).thenReturn(false);
            platformMockedStatic.when(() -> Platform.isMac()).thenReturn(true);

            assertFalse(mediaInfoUnderTest.preferOpenViaBuffer("AnAsciiPath.mkv"));
        }
    }

    @Test
    public void preferOpenViaBuffer_withLinuxPlatform_shouldReturnFalse() {
        try (MockedStatic<Platform> platformMockedStatic = mockStatic(Platform.class)) {
            platformMockedStatic.when(() -> Platform.isWindows()).thenReturn(false);
            platformMockedStatic.when(() -> Platform.isMac()).thenReturn(false);

            assertFalse(mediaInfoUnderTest.preferOpenViaBuffer("AnAsciiPath.mkv"));
        }
    }

    //////////////
    // parseTime
    //////////////

    @Test
    public void parseTime_withValidTime_shouldReturnInstant() {
        final Instant expected = ZonedDateTime.of(2020, 4, 15, 12, 30, 0, 0, ZoneId.of("UTC")).toInstant();

        final Instant actual = MediaInfoBase.parseTime("2020-04-15 12:30:00 UTC");

        assertEquals(expected, actual);
    }

    @Test
    public void parseTime_withInvalidTime_shouldReturnNull() {
        assertAll(
                () -> assertNull(MediaInfoBase.parseTime(null)),
                () -> assertNull(MediaInfoBase.parseTime(StringUtils.EMPTY)));
    }

    //////////////
    // parseList
    //////////////

    @Test
    public void parseList_withValidList_shouldReturnList() {
        final List<String> expected = List.of("Value 1", "Value 2", "Value 3");

        final List<String> actual = MediaInfoBase.parseList("Value 1 / Value 2 / Value 3");

        assertEquals(expected, actual);
    }

    @Test
    public void parseList_withBlankEntry_shouldReturnList() {
        final List<String> expected = List.of("Value 1", "Value 2");

        final List<String> actual = MediaInfoBase.parseList("Value 1 / / Value 2");

        assertEquals(expected, actual);
    }

    @Test
    public void parseList_withBlankListValue_shouldReturnEmptyList() {
        assertAll(
                () -> assertTrue(MediaInfoBase.parseList(null).isEmpty()),
                () -> assertTrue(MediaInfoBase.parseList(StringUtils.EMPTY).isEmpty()));
    }
}
