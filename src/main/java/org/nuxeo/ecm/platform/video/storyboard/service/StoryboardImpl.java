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

import java.util.ArrayList;
import java.util.List;

public class StoryboardImpl implements Storyboard {

    List<Frame> frames = new ArrayList<>();

    @Override
    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    @Override
    public void addAllFrames(List<Frame> frames) {
        this.frames.addAll(frames);
    }

    @Override
    public int size() {
        return frames.size();
    }

    @Override
    public List<Frame> getFrames() {
        return frames;
    }

    @Override
    public String toString() {
        return "StoryboardImpl{" +
                "frames=" + frames +
                '}';
    }
}
