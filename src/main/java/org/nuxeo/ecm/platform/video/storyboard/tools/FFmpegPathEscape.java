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

package org.nuxeo.ecm.platform.video.storyboard.tools;

import org.apache.commons.lang3.SystemUtils;

/**
 * Created by Michaël on 3/22/2016.
 */
public class FFmpegPathEscape {

    public static String escape(String path) {
        if (SystemUtils.IS_OS_WINDOWS) {
            path = path.replaceAll("\\\\","\\\\\\\\\\\\\\\\");
            path = path.replaceAll(":","\\\\\\\\:");
        }
        return path;

    }

}
