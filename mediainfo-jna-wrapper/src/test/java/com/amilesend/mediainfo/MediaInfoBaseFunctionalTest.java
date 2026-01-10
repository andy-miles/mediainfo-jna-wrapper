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
package com.amilesend.mediainfo;

import com.amilesend.mediainfo.lib.MediaInfoAccessor;
import com.amilesend.mediainfo.lib.MediaInfoLibrary;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MediaInfoBaseFunctionalTest {
    private static final String SAMPLE_MKV_VIDEO_PATH = "src/test/SampleVideo_1280x720_1mb.mkv";
    private static final String SAMPLE_MP4_VIDEO_PATH = "src/test/SampleVideo_1280x720_1mb.mp4";
    private MyTestVideoInfo mkvMediaInfoUnderTest;
    private MyTestVideoInfo mp4MediaInfoUnderTest;

    @BeforeEach
    @SneakyThrows
    public void setUp() {
        final MediaInfoLibrary library = MediaInfoLibrary.newInstance();
        final MediaInfoAccessor mkvAccessor = new MediaInfoAccessor(library);
        mkvMediaInfoUnderTest = new MyTestVideoInfo(mkvAccessor).open(new File(SAMPLE_MKV_VIDEO_PATH));

        final MediaInfoAccessor mp4Accessor = new MediaInfoAccessor(library);
        mp4MediaInfoUnderTest = new MyTestVideoInfo(mp4Accessor).open(new File(SAMPLE_MP4_VIDEO_PATH));
    }

    @AfterEach
    public void cleanUp() {
        mkvMediaInfoUnderTest.close();
        mp4MediaInfoUnderTest.close();
    }

    @Test
    public void getVideoCodecs_withMkvFile_shouldReturnCodecs() {
        assertEquals(List.of("MPEG-4 Visual"), mkvMediaInfoUnderTest.getVideoCodecs());
    }

    @Test
    public void getDuration_withMkvFile_shouldReturnDuration() {
        assertEquals(Duration.ofMillis(3600), mkvMediaInfoUnderTest.getDuration());
    }

    @Test
    public void getWidth_withMkvFile_shouldReturnWidth() {
        assertEquals(1280, mkvMediaInfoUnderTest.getWidth());
    }

    @Test
    public void getHeight_withMkvFile_shouldReturnHeight() {
        assertEquals(720, mkvMediaInfoUnderTest.getHeight());
    }

    @Test
    public void getVideoCodecs_withMp4File_shouldReturnCodecs() {
        assertEquals(List.of("AVC"), mp4MediaInfoUnderTest.getVideoCodecs());
    }

    @Test
    public void getDuration_withMp4File_shouldReturnDuration() {
        assertEquals(Duration.ofMillis(5312), mp4MediaInfoUnderTest.getDuration());
    }

    @Test
    public void getWidth_withMp4File_shouldReturnWidth() {
        assertEquals(1280, mp4MediaInfoUnderTest.getWidth());
    }

    @Test
    public void getHeight_withMp4File_shouldReturnHeight() {
        assertEquals(720, mp4MediaInfoUnderTest.getHeight());
    }

    @Test
    public void getCreationTime_withMp4File_shouldReturnTime() {
        assertEquals(
                ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                mp4MediaInfoUnderTest.getCreationTime());
    }
}
