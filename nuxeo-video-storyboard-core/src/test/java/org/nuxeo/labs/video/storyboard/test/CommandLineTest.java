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

package org.nuxeo.labs.video.storyboard.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandNotAvailable;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.labs.video.storyboard.tools.FFmpegPathEscape;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.labs.video.storyboard",
        "org.nuxeo.ecm.platform.commandline.executor"
})
public class CommandLineTest {

    @Inject
    CoreSession session;

    @Inject
    protected CommandLineExecutorService cmdService;

    @Test
    public void testGetFramesTimestamps() throws IOException, CommandNotAvailable, URISyntaxException {
        Blob outputBlob = Blobs.createBlobWithExtension("");
        CmdParameters params = cmdService.getDefaultCmdParameters();
        String inputPath = Paths.get(getClass().getResource("/files/TourEiffel.mp4").toURI()).toString();
        inputPath = FFmpegPathEscape.escape(inputPath);
        params.addNamedParameter("inFilePath", inputPath);
        params.addNamedParameter("outFilePath", outputBlob.getFile().getAbsolutePath());
        params.addNamedParameter("from", String.valueOf(10));
        params.addNamedParameter("to", String.valueOf(20));
        params.addNamedParameter("nb", String.valueOf(5));
        ExecResult result = cmdService.execCommand("ffmpeg-extract-frames-timestamp", params);
        if (!result.isSuccessful()) {
            System.out.println(result.getError());
        }
        Assert.assertTrue(result.isSuccessful());
    }

    @Test
    public void testIdentify() throws IOException, CommandNotAvailable, URISyntaxException {
        CmdParameters params = cmdService.getDefaultCmdParameters();
        String inputPath = Paths.get(getClass().getResource("/files/plane2.jpg").toURI()).toString();
        params.addNamedParameter("inFilePath", inputPath);
        ExecResult result = cmdService.execCommand("imagemagick-identify", params);
        if (!result.isSuccessful()) {
            System.out.println(result.getError());
        }
        Assert.assertTrue(result.isSuccessful());
    }

    @Test
    public void testConvertToEdges() throws IOException, CommandNotAvailable, URISyntaxException {
        Blob outBlob = Blobs.createBlobWithExtension(".jpg");
        CmdParameters params = cmdService.getDefaultCmdParameters();
        String inputPath = Paths.get(getClass().getResource("/files/NONAME").toURI()).toString();
        params.addNamedParameter("inFilePath", inputPath);
        params.addNamedParameter("outFilePath", outBlob.getFile().getAbsolutePath());
        ExecResult result = cmdService.execCommand("imagemagick-edges", params);
        if (!result.isSuccessful()) {
            System.out.println(result.getError());
        }
        Assert.assertTrue(result.isSuccessful());
    }

}
