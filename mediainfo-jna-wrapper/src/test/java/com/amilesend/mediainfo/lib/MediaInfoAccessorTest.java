/*
 * The MIT License
 * Copyright © 2024-2026 Andy Miles
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
import com.amilesend.mediainfo.type.StreamType;
import com.amilesend.mediainfo.util.StringUtils;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MediaInfoAccessorTest {

    @Mock
    private MediaInfoLibrary mockLibrary;
    @Mock
    private Pointer mockPointer;
    private MediaInfoAccessor accessorUnderTest;

    @BeforeEach
    public void setUp() {
        when(mockLibrary.newHandle()).thenReturn(mockPointer);
        accessorUnderTest = spy(new MediaInfoAccessor(mockLibrary));
    }

    /////////
    // ctor
    /////////

    @Test
    public void ctor_withLinkageException_shouldThrowException() {
        when(mockLibrary.newHandle()).thenThrow(new LinkageError("Error!"));

        final Throwable thrown = assertThrows(MediaInfoException.class,
                () -> new MediaInfoAccessor(mockLibrary));
        assertInstanceOf(LinkageError.class, thrown.getCause());
    }

    //////////
    // close
    //////////

    @Test
    public void close_shouldInvokeDispose() {
        doNothing().when(accessorUnderTest).dispose();

        accessorUnderTest.close();

        verify(accessorUnderTest).dispose();
    }

    ////////////
    // dispose
    ////////////

    @Test
    public void dispose_withValidPointer_shouldCloseAndDeleteHandle() {
        accessorUnderTest.dispose();

        assertAll(
                () -> verify(mockLibrary).close(eq(mockPointer)),
                () -> verify(mockLibrary).deleteHandle(eq(mockPointer)),
                () -> assertNull(accessorUnderTest.getMediaInfoLibPointer()));
    }

    @Test
    public void dispose_withNullPointer_shouldNotCloseAndDeleteHandle() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        accessorUnderTest.dispose();

        assertAll(
                () -> verify(mockLibrary, never()).close(any(Pointer.class)),
                () -> verify(mockLibrary, never()).deleteHandle(any(Pointer.class)),
                () -> assertNull(accessorUnderTest.getMediaInfoLibPointer()));
    }

    /////////
    // open
    /////////

    @Test
    public void open_withValidFileName_shouldReturnResponse() {
        when(mockLibrary.open(any(Pointer.class), any(WString.class))).thenReturn(1);

        final boolean actual = accessorUnderTest.open("someValidFile");

        assertAll(
                () -> verify(mockLibrary).open(isA(Pointer.class), isA(WString.class)),
                () -> assertTrue(actual));
    }

    @Test
    public void open_withValidFileNameAndNullPointer_shouldReturnResponse() {
        accessorUnderTest.setMediaInfoLibPointer(null);
        open_withValidFileName_shouldReturnResponse();
    }

    @Test
    public void open_withInvalidFileName_shouldThrowException() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> accessorUnderTest.open(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> accessorUnderTest.open(StringUtils.EMPTY)));
    }

    ///////////////////
    // openBufferInit
    ///////////////////

    @Test
    public void openBufferInit_withValidLengthAndOffset_shouldReturnResponse() {
        when(mockLibrary.openBufferInit(any(Pointer.class), anyLong(), anyLong())).thenReturn(1);

        final boolean actual = accessorUnderTest.openBufferInit(1000L, 0L);

        assertAll(
                () -> verify(mockLibrary).openBufferInit(isA(Pointer.class), eq(1000L), eq(0L)),
                () -> assertTrue(actual));
    }

    @Test
    public void openBufferInit_withInvalidLengthAndOffset_shouldThrowException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.openBufferInit(-1L, 0L)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.openBufferInit(1000L, -1L)));
    }

    @Test
    public void openBufferInit_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.openBufferInit(1000L, 0L));
    }

    ///////////////////////
    // openBufferContinue
    ///////////////////////

    @Test
    public void openBufferContinue_withValidBufferAndSize_shouldReturnResponse() {
        final int expected = 0;
        final byte[] buffer = new byte[512];
        final int size = 256;
        when(mockLibrary.openBufferContinue(any(Pointer.class), any(byte[].class), anyInt())).thenReturn(expected);

        final int actual = accessorUnderTest.openBufferContinue(buffer, size);

        assertAll(
                () -> verify(mockLibrary).openBufferContinue(isA(Pointer.class), eq(buffer), eq(size)),
                () -> assertEquals(expected, actual));
    }

    @Test
    public void openBufferContinue_withInvalidBufferAndSize_shouldThrowException() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.openBufferContinue(null, 256)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.openBufferContinue(new byte[4], -1)));
    }

    @Test
    public void openBufferContinue_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.openBufferContinue(new byte[512], 256));
    }

    //////////////////////////////
    // openBufferContinueGotoGet
    //////////////////////////////

    @Test
    public void openBufferContinueGotoGet_shouldInvokeLibrary() {
        accessorUnderTest.openBufferContinueGotoGet();

        verify(mockLibrary).openBufferContinueGotoGet(isA(Pointer.class));
    }

    @Test
    public void openBufferContinueGotoGet_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.openBufferContinueGotoGet());
    }

    ///////////////////////
    // openBufferFinalize
    ///////////////////////

    @Test
    public void openBufferFinalize_shouldInvokeLibrary() {
        accessorUnderTest.openBufferFinalize();

        verify(mockLibrary).openBufferFinalize(isA(Pointer.class));
    }

    @Test
    public void openBufferFinalize_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.openBufferFinalize());
    }

    ////////////////
    // closeHandle
    ////////////////

    @Test
    public void closeHandle_shouldInvokeLibrary() {
        accessorUnderTest.closeHandle();

        verify(mockLibrary).close(isA(Pointer.class));
    }

    @Test
    public void closeHandle_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.closeHandle());
    }

    ///////////
    // inform
    ///////////

    @Test
    public void inform_shouldReturnResponse() {
        final String expected = "Response";
        final WString response = mock(WString.class);
        when(response.toString()).thenReturn(expected);
        when(mockLibrary.inform(any(Pointer.class), anyInt())).thenReturn(response);

        final String actual = accessorUnderTest.inform();

        assertAll(
                () -> verify(mockLibrary).inform(isA(Pointer.class), eq(0)),
                () -> assertEquals(expected, actual));
    }

    @Test
    public void inform_withNullMediaInfoLibrary_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class, () -> accessorUnderTest.inform());
    }

    /////////////////////////////////
    // get(StreamType, int, String)
    /////////////////////////////////

    @Test
    public void get_withParameter_shouldReturnResponse() {
        final String expected = "Result";
        doReturn(expected).when(accessorUnderTest).get(
                any(StreamType.class),
                anyInt(),
                any(String.class),
                any(InfoType.class),
                any(InfoType.class));

        final String actual = accessorUnderTest.get(StreamType.General, 0, "Parameter");

        assertAll(
                () -> verify(accessorUnderTest).get(
                        eq(StreamType.General),
                        eq(0),
                        eq("Parameter"),
                        eq(InfoType.Text),
                        eq(InfoType.Name)),
                () -> assertEquals(expected, actual));
    }

    ///////////////////////////////////////////
    // get(StreamType, int, String, InfoType)
    ///////////////////////////////////////////

    @Test
    public void get_withParameterAndInfoType_shouldReturnResponse() {
        final String expected = "Result";
        doReturn(expected).when(accessorUnderTest).get(
                any(StreamType.class),
                anyInt(),
                any(String.class),
                any(InfoType.class),
                any(InfoType.class));

        final String actual = accessorUnderTest.get(StreamType.General, 0, "Parameter", InfoType.Measure);

        assertAll(
                () -> verify(accessorUnderTest).get(
                        eq(StreamType.General),
                        eq(0),
                        eq("Parameter"),
                        eq(InfoType.Measure),
                        eq(InfoType.Name)),
                () -> assertEquals(expected, actual));
    }

    /////////////////////////////////////////////////////
    // get(StreamType, int, String, InfoType, InfoType)
    /////////////////////////////////////////////////////

    @Test
    public void get_withParameterAndInfoTypeAndSearchType_shouldReturnResponse() {
        final String expected = "Result";
        final WString response = mock(WString.class);
        when(response.toString()).thenReturn(expected);
        when(mockLibrary.get(
                any(Pointer.class),
                anyInt(),
                anyInt(),
                any(WString.class),
                anyInt(),
                anyInt())).thenReturn(response);

        final String actual =
                accessorUnderTest.get(StreamType.General, 0, "Parameter", InfoType.Measure, InfoType.Info);

        assertAll(
                () -> verify(mockLibrary).get(
                        isA(Pointer.class),
                        anyInt(),
                        anyInt(),
                        isA(WString.class),
                        anyInt(),
                        anyInt()),
                () -> assertEquals(expected, actual));
    }

    @Test
    public void get_withParameterAndInvalidInput_shouldThrowException() {
        final String parameter = "Parameter";

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                null, // streamType
                                0,
                                parameter,
                                InfoType.Text,
                                InfoType.Name)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                -1, // streamNumber
                                parameter,
                                InfoType.Text,
                                InfoType.Name)),
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                null, // parameter
                                InfoType.Text,
                                InfoType.Name)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                StringUtils.EMPTY, // parameter
                                InfoType.Text,
                                InfoType.Name)),
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                parameter,
                                null, // infoType
                                InfoType.Name)),
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                parameter,
                                InfoType.Text,
                                null))); // searchType
    }

    @Test
    public void get_withParameterAndNullPointer_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class,
                () -> accessorUnderTest.get(
                        StreamType.General,
                        0,
                        "Parameter",
                        InfoType.Measure,
                        InfoType.Info));
    }

    //////////////////////////////
    // get(StreamType, int, int)
    //////////////////////////////

    @Test
    public void get_withParameterIndex_shouldReturnResponse() {
        final String expected = "Result";
        doReturn(expected).when(accessorUnderTest).get(
                any(StreamType.class),
                anyInt(),
                anyInt(),
                any(InfoType.class));

        final String actual = accessorUnderTest.get(StreamType.General, 0, 1);

        assertAll(
                () -> verify(accessorUnderTest).get(
                        eq(StreamType.General),
                        eq(0),
                        eq(1),
                        eq(InfoType.Text)),
                () -> assertEquals(expected, actual));
    }

    ////////////////////////////////////////
    // get(StreamType, int, int, InfoType)
    ////////////////////////////////////////

    @Test
    public void get_withParameterIndexAndInfoType_shouldReturnResponse() {
        final String expected = "Result";
        final WString response = mock(WString.class);
        when(response.toString()).thenReturn(expected);
        when(mockLibrary.getI(
                any(Pointer.class),
                anyInt(),
                anyInt(),
                anyInt(),
                anyInt())).thenReturn(response);

        final String actual = accessorUnderTest.get(StreamType.General, 0, 1, InfoType.Name);

        assertAll(
                () -> verify(mockLibrary).getI(
                        isA(Pointer.class),
                        eq(StreamType.General.ordinal()),
                        eq(0),
                        eq(1),
                        eq(InfoType.Name.ordinal())),
                () -> assertEquals(expected, actual));
    }

    @Test
    public void get_withParameterIndexAndInvalidInput_shouldThrowException() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                null, // streamType
                                0,
                                1,
                                InfoType.Text)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                -1, // streamNumber
                                1,
                                InfoType.Text)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                -1, // parameterIndex
                                InfoType.Text)),
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.get(
                                StreamType.General,
                                0,
                                1,
                                null))); // infoType
    }

    @Test
    public void get_withParameterIndexAndNullPointer_shouldThrowException() {
        accessorUnderTest.setMediaInfoLibPointer(null);

        assertThrows(IllegalStateException.class,
                () -> accessorUnderTest.get(StreamType.General, 0, 1, InfoType.Name));
    }

    ///////////////////
    // getStreamCount
    ///////////////////

    @Test
    public void getStreamCount_withStreamTypeAndResponse_shouldReturnCount() {
        doReturn("4").when(accessorUnderTest).get(any(StreamType.class), anyInt(), anyString());

        final int actual = accessorUnderTest.getStreamCount(StreamType.Audio);

        assertAll(
                () -> assertEquals(4, actual),
                () -> verify(accessorUnderTest).get(eq(StreamType.Audio), eq(0), eq("StreamCount")));
    }

    @Test
    public void getStreamCount_withStreamTypeAndEmptyResponse_shouldReturnZero() {
        doReturn(StringUtils.EMPTY).when(accessorUnderTest).get(any(StreamType.class), anyInt(), anyString());

        final int actual = accessorUnderTest.getStreamCount(StreamType.Image);

        assertAll(
                () -> assertEquals(0, actual),
                () -> verify(accessorUnderTest).get(eq(StreamType.Image), eq(0), eq("StreamCount")));
    }

    @Test
    public void getStreamCount_withNullStreamType_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> accessorUnderTest.getStreamCount(null));
    }

    //////////////////////////////
    // getStreamOrParameterCount
    //////////////////////////////

    @Test
    public void getStreamOrParameterCount_withValidStreamTypeAndNumber_shouldReturnResponse() {
        final int expected = 10;
        when(mockLibrary.countGet(any(Pointer.class), anyInt(), anyInt())).thenReturn(expected);

        final int actual = accessorUnderTest.getStreamOrParameterCount(StreamType.General, 0);

        assertAll(
                () -> assertEquals(expected, actual),
                () -> verify(mockLibrary).countGet(isA(Pointer.class), eq(StreamType.General.ordinal()), eq(0)));
    }

    @Test
    public void getStreamOrParameterCount_withInvalidInput_shouldThrowException() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.getStreamOrParameterCount(null, 0)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.getStreamOrParameterCount(StreamType.General, -1)));
    }

    //////////////
    // getOption
    //////////////

    @Test
    public void getOption_withValidOption_shouldReturnResponse() {
        final String expected = "Response";
        doReturn(expected).when(accessorUnderTest).setOption(anyString(), anyString());

        final String actual = accessorUnderTest.getOption("Option Name");

        assertAll(
                () -> assertEquals(expected, actual),
                () -> verify(accessorUnderTest).setOption(eq("Option Name"), eq(StringUtils.EMPTY)));
    }

    //////////////
    // setOption
    //////////////

    @Test
    public void setOption_withOptionAndValue_shouldReturnResponse() {
        final String expected = "Response";
        final WString response = mock(WString.class);
        when(response.toString()).thenReturn(expected);
        when(mockLibrary.option(any(Pointer.class), any(WString.class), any(WString.class))).thenReturn(response);

        final String actual = accessorUnderTest.setOption("Optional Name", "Optional Value");

        assertAll(
                () -> assertEquals(expected, actual),
                () -> verify(mockLibrary).option(eq(mockPointer), isA(WString.class), isA(WString.class)));
    }

    @Test
    public void setOption_withInvalidInput_shouldThrowException() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.setOption(null, "Value")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> accessorUnderTest.setOption(StringUtils.EMPTY, "Value")),
                () -> assertThrows(NullPointerException.class,
                        () -> accessorUnderTest.setOption("Option Name", null)));
    }
}
