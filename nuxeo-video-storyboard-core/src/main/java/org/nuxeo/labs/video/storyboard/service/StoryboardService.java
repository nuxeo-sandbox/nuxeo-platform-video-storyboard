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

package org.nuxeo.labs.video.storyboard.service;

import org.nuxeo.ecm.platform.video.Video;


public interface StoryboardService {

    /**
     *
     * @param video The input video
     * @param nbFrames The number of frames
     * @return A Storyboard object
     */
    Storyboard generateStoryboard(Video video, int nbFrames) throws Exception;


    /**
     *
     * @return the automation chain to use in the default listener
     */
    String getDefaultChain();


    /**
     *
     * @return the maximum number of frames to generate for a video
     */
    int getMaxFrames();


    /**
     *
     * @return the minimum step between to frames in seconds
     */
    int getMinStepInSeconds();

}
