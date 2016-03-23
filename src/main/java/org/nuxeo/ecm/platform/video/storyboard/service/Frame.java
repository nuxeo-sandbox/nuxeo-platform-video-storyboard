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

public class Frame {

    protected Blob frame;
    protected double time;
    protected String comment;

    public Frame() {}

    public Frame(Blob frame, double time, String comment) {
        this.frame = frame;
        this.time = time;
        this.comment = comment;
    }


    public Blob getFrame() {
        return frame;
    }


    public void setFrame(Blob frame) {
        this.frame = frame;
    }


    public double getTimeInSeconds() {
        return time;
    }


    public void setTimeInSeconds(double time) {
        this.time = time;
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "FrameImpl{" +
                "frame=" + frame +
                ", time=" + time +
                ", comment='" + comment + '\'' +
                '}';
    }
}
