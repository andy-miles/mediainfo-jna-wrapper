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
import com.amilesend.mediainfo.type.StreamType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Sample {@link MediaInfoBase} implementation exmaple used for testing.
 *
 * @see MediaInfoBase
 */
public class MyTestVideoInfo extends MediaInfoBase<MyTestVideoInfo> {
    private static final String VIDEO_CODEC_LIST = "Video_Codec_List";
    private static final String DURATION = "Duration";
    private static final String WIDTH = "Width";
    private static final String HEIGHT = "Height";
    private static final String ENCODED_DATE = "Encoded_Date";

    public MyTestVideoInfo(final MediaInfoAccessor accessor) {
        super(accessor);
    }

    public List<String> getVideoCodecs() {
        final String codecs = getAccessor().get(StreamType.General, 0, VIDEO_CODEC_LIST);
        return parseList(codecs);
    }

    public Duration getDuration() {
        final long value = (long) Double.parseDouble(getAccessor().get(StreamType.General, 0, DURATION));
        return Duration.ofMillis(value);
    }

    public int getWidth() {
        return Integer.parseInt(getAccessor().get(StreamType.Video, 0, WIDTH));
    }

    public int getHeight() {
        return Integer.parseInt(getAccessor().get(StreamType.Video, 0, HEIGHT));
    }

    public Instant getCreationTime() {
        final String encodingTime = getAccessor().get(StreamType.General, 0, ENCODED_DATE);
        return parseTime(encodingTime);
    }
}
