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
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.labs.video.storyboard.service.Frame;
import org.nuxeo.labs.video.storyboard.service.Storyboard;
import org.nuxeo.labs.video.storyboard.service.StoryboardService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.labs.video.storyboard",
        "org.nuxeo.ecm.platform.commandline.executor",
        "org.nuxeo.ecm.platform.video.core",
        "org.nuxeo.ecm.platform.video.convert",
})
@LocalDeploy({
        "nuxeo-vision-core:OSGI-INF/disable-conversion-listener-contrib.xml"
})
public class StoryboardServiceTest {

    @Inject
    CoreSession session;

    @Inject
    StoryboardService storyboardService;

    @Test
    public void testGetStoryboard() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);
        VideoDocument videoDoc = doc.getAdapter(VideoDocument.class);
        Storyboard storyboard = storyboardService.generateStoryboard(videoDoc.getVideo(),10);
        Assert.assertEquals(10,storyboard.size());
        System.out.println(storyboard);

        //save files
        int i=0;
        for (Frame frame : storyboard.getFrames()) {
            Files.copy(frame.getBlob().getStream(),
                    Paths.get(new File(getClass().getResource("/").getPath()).getPath(),"blob"+i+".jpg"));
            i++;
        }

    }

}
