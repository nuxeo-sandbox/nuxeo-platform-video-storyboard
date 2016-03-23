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

package org.nuxeo.ecm.platform.video.storyboard.adapter;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.video.VideoConstants;
import org.nuxeo.ecm.platform.video.storyboard.service.Frame;
import org.nuxeo.ecm.platform.video.storyboard.service.Storyboard;

import java.io.Serializable;
import java.util.*;


public class StoryboardAdapter implements Storyboard {

    private static final Log log = LogFactory.getLog(StoryboardAdapter.class);

    private final DocumentModel doc;

    private SortedSet<Frame> frames = new TreeSet<>(new Comparator<Frame>() {
        @Override
        public int compare(Frame f1, Frame f2) {
            if (f1.getTimeInSeconds()<f2.getTimeInSeconds()) {
                return -1;
            } else if (f1.getTimeInSeconds()==f2.getTimeInSeconds()) {
                return 0;
            } else {
                return 1;
            }
        }
    });

    public StoryboardAdapter(DocumentModel doc) {
        this.doc = doc;
        List<Map<String,Serializable>> property =
                (List<Map<String, Serializable>>) doc.getPropertyValue(VideoConstants.STORYBOARD_PROPERTY);
        if (property!=null) {
            for (Map<String,Serializable> item : property) {
                Frame frame = new Frame();
                frame.setFrame((Blob) item.get("content"));
                frame.setTimeInSeconds((Double) item.get("timecode"));
                frame.setComment((String) item.get("comment"));
                frames.add(frame);
            }
        }
    }

    @Override
    public void addFrame(Frame frame) {
        frames.add(frame);
        updateDoc();
    }

    @Override
    public void addAllFrames(List<Frame> frames) {
        this.frames.addAll(frames);
        updateDoc();
    }

    @Override
    public int size() {
        return frames.size();
    }

    @Override
    public List<Frame> getFrames() {
        List list = new ArrayList<>();
        list.addAll(frames);
        return list;
    }

    protected void updateDoc() {
        List<Map<String,Serializable>> property = new ArrayList<>();
        for (Frame frame : frames) {
            Map<String,Serializable> item = new HashedMap();
            item.put("content", (Serializable) frame.getFrame());
            item.put("timecode",frame.getTimeInSeconds());
            item.put("comment",frame.getComment());
            property.add(item);
        }
        doc.setPropertyValue(VideoConstants.STORYBOARD_PROPERTY, (Serializable) property);
    }

}