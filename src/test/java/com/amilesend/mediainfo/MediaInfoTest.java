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
import com.amilesend.mediainfo.type.InfoType;
import com.amilesend.mediainfo.type.StreamType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MediaInfoTest {
    @Mock
    private MediaInfoAccessor mockAccessor;
    @InjectMocks
    private MediaInfo mediaInfoUnderTest;

    @Test
    public void inform_shouldInvokeAccessor() {
        mediaInfoUnderTest.inform();

        verify(mockAccessor).inform();
    }

    @Test
    public void get_withParameter_shouldInvokeAccessor() {
        mediaInfoUnderTest.get(StreamType.General, 0, "TestParameter");

        verify(mockAccessor).get(eq(StreamType.General), eq(0), eq("TestParameter"));
    }

    @Test
    public void get_withParameterAndInfoType_shouldInvokeAccessor() {
        mediaInfoUnderTest.get(StreamType.General, 0, "TestParameter", InfoType.Text);

        verify(mockAccessor).get(eq(StreamType.General), eq(0), eq("TestParameter"), eq(InfoType.Text));
    }

    @Test
    public void get_withParameterAndSearchType_shouldInvokeAccessor() {
        mediaInfoUnderTest.get(StreamType.General, 0, "TestParameter", InfoType.Name, InfoType.Text);

        verify(mockAccessor).get(
                eq(StreamType.General),
                eq(0),
                eq("TestParameter"),
                eq(InfoType.Name),
                eq(InfoType.Text));
    }

    @Test
    public void get_withParameterIndex_shouldInvokeAccessor() {
        mediaInfoUnderTest.get(StreamType.General, 0, 1);

        verify(mockAccessor).get(eq(StreamType.General), eq(0), eq(1));
    }

    @Test
    public void get_withParameterIndexAndType_shouldInvokeAccessor() {
        mediaInfoUnderTest.get(StreamType.General, 0, 1, InfoType.Text);

        verify(mockAccessor).get(eq(StreamType.General), eq(0), eq(1), eq(InfoType.Text));
    }

    @Test
    public void getStreamCount_shouldInvokeAccessor() {
        mediaInfoUnderTest.getStreamCount(StreamType.Video);

        verify(mockAccessor).getStreamCount(eq(StreamType.Video));
    }

    @Test
    public void getStreamOrParameterCount_shouldInvokeAccessor() {
        mediaInfoUnderTest.getStreamOrParameterCount(StreamType.Video, 0);

        verify(mockAccessor).getStreamOrParameterCount(eq(StreamType.Video), eq(0));
    }

    @Test
    public void getOption_shouldInvokeAccessor() {
        mediaInfoUnderTest.getOption("Option");

        verify(mockAccessor).getOption(eq("Option"));
    }

    @Test
    public void setOption_shouldInvokeAccessor() {
        mediaInfoUnderTest.setOption("Option Name", "Option Value");

        verify(mockAccessor).setOption(eq("Option Name"), eq("Option Value"));
    }
}
