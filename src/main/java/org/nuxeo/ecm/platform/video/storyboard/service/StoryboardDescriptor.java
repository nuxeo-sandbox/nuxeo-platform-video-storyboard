/*
 * (C) Copyright 2015-2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Michael Vachette
 */

package org.nuxeo.ecm.platform.video.storyboard.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;


@XObject("configuration")
public class StoryboardDescriptor {

    @XNode("defaultListenerChainName")
    private String defaultListenerChainName="javascript.StoryboardListenerChain";

    @XNode("minStd")
    private double minStd=5000.0;

    @XNode("minStepInSeconds")
    private int minStepInSeconds=5;

    @XNode("maxFrames")
    private int maxFrames=30;

    @XNode("width")
    private int width=240;

    @XNode("height")
    private int height=135;


    public String getDefaultListenerChainName() {
        return defaultListenerChainName;
    }

    public double getMinStd() {
        return minStd;
    }

    public int getMinStepInSeconds() {
        return minStepInSeconds;
    }

    public int getMaxFrames() {
        return maxFrames;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}