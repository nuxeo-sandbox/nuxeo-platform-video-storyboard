/*
 * (C) Copyright 2006-2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Michael Vachette <mvachette@nuxeo.com>
 */

package org.nuxeo.ecm.platform.video.storyboard.service;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandNotAvailable;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.ecm.platform.video.Video;
import org.nuxeo.ecm.platform.video.convert.Constants;
import org.nuxeo.ecm.platform.video.storyboard.tools.FFmpegPathEscape;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StoryboardServiceImpl extends DefaultComponent implements StoryboardService {

    protected static final Pattern FFPROBE_ITEM_PATTERN =
            Pattern.compile("^.*pkt_pts_time=(?<time>[0-9.]*).*$");

    protected static final String TIME_GROUP = "time";

    protected static final int STANDARD_DEVIATION_MIN = 3500;


    @Override
    public Storyboard generateStoryboard(Video video, int nbFrames) throws Exception {
        Storyboard storyboard = new StoryboardImpl();
        List<Frame> frames = getMainFrames(video);
        if (frames.size()==0) return storyboard;
        frames = filterFrames(video,frames,nbFrames);
        storyboard.addAllFrames(frames);
        return storyboard;
    }

    protected List<Frame> getMainFrames(Video video) throws NuxeoException, CommandNotAvailable {
        CommandLineExecutorService cmdService = Framework.getService(CommandLineExecutorService.class);
        CmdParameters params = cmdService.getDefaultCmdParameters();
        String inputPath = Paths.get(video.getBlob().getFile().getAbsolutePath()).toString();
        inputPath = FFmpegPathEscape.escape(inputPath);
        params.addNamedParameter("inFilePath", inputPath);
        params.addNamedParameter("from", String.valueOf(10));
        params.addNamedParameter("to", String.valueOf(20));
        params.addNamedParameter("nb", String.valueOf(5));
        ExecResult result = cmdService.execCommand("ffmpeg-extract-frames-timestamp", params);
        if (!result.isSuccessful()) {
            throw new NuxeoException("FFprobe failed",result.getError());
        }
        return convertFFprobeOutput(result.getOutput());
    }

    protected List<Frame> convertFFprobeOutput(List<String> output) {
        List<Frame> frames = new ArrayList<>();
        for (String line : output) {
            Matcher m = FFPROBE_ITEM_PATTERN.matcher(line);
            if (!m.matches()) continue;
            Frame frame = new Frame();
            double time = Double.parseDouble(m.group(TIME_GROUP));
            frame.setTimeInSeconds(time);
            frames.add(frame);
        }
        return frames;
    }

    protected List<Frame> filterFrames(Video video,List<Frame> input,int nbFrames) throws CommandNotAvailable {
        List<Frame> filtered = new ArrayList<>();
        int inputSize = input.size();
        int step = 1;
        if (inputSize>nbFrames) step = Math.round(inputSize/(nbFrames*1.0f));
        int start = 1;
        while (start < inputSize) {
            int end = start-1+step >= inputSize ? inputSize-1 : start-1+step;
            List<Frame> range = input.subList(start-1,end);
            Frame selected = getFirstRelevantFrameInRange(video,range);
            if (selected!=null) filtered.add(selected);
            start+=step;
        }
        return filtered;
    }

    protected Frame getFirstRelevantFrameInRange(Video video, List<Frame> frames) throws CommandNotAvailable {
        Frame selected = null;
        for (Frame frame : frames) {
            Blob blob = extractFrameFromVideo(video,frame.getTimeInSeconds());
            if (isFrameRelevant(blob)) {
                frame.setBlob(blob);
                selected = frame;
                break;
            }
        }
        return selected;
    }

    protected Blob extractFrameFromVideo(Video video, double timecode) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put(Constants.POSITION_PARAMETER, timecode);
        BlobHolder result;
        ConversionService conversionService = Framework.getService(ConversionService.class);
        result = conversionService.convert(Constants.SCREENSHOT_CONVERTER,
                    new SimpleBlobHolder(video.getBlob()), parameters);
        return result.getBlob();
    }

    protected boolean isFrameRelevant(Blob frame) throws CommandNotAvailable {
        CommandLineExecutorService cmdService = Framework.getService(CommandLineExecutorService.class);
        CmdParameters params = cmdService.getDefaultCmdParameters();
        String inputPath = Paths.get(frame.getFile().getAbsolutePath()).toString();
        params.addNamedParameter("inFilePath", inputPath);
        ExecResult result = cmdService.execCommand("imagemagick-identify", params);
        if (!result.isSuccessful()) {
            throw new NuxeoException("FFprobe failed",result.getError());
        }
        double std = Double.parseDouble(result.getOutput().get(0));
        return (std > STANDARD_DEVIATION_MIN);
    }

}
