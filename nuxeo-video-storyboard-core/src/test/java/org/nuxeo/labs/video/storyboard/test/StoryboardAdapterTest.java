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
import org.nuxeo.labs.video.storyboard.adapter.StoryboardAdapter;
import org.nuxeo.labs.video.storyboard.service.Frame;
import org.nuxeo.labs.video.storyboard.service.Storyboard;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.labs.video.storyboard",
        "org.nuxeo.ecm.platform.video.core"
})
@LocalDeploy({
        "nuxeo-vision-core:OSGI-INF/disable-conversion-listener-contrib.xml"
})
public class StoryboardAdapterTest {

    @Inject
    CoreSession session;

    @Test
    public void testGetAdapterWithVideoDoc() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);
        Storyboard adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertNotNull(adapter);
    }

    @Test
    public void testGetAdapterWithFileDoc() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "File", "File");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);
        Storyboard adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertNull(adapter);
    }

    @Test
    public void testGetAdapterUpdateOneFrame() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);

        Storyboard adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertEquals(0,adapter.size());
        Frame frame = new Frame(blob,1.0,"test");
        adapter.addFrame(frame);

        doc = session.saveDocument(doc);
        adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertEquals(1,adapter.size());
        Frame savedFrame = adapter.getFrames().get(0);
        Assert.assertEquals(frame.getTimeInSeconds(),savedFrame.getTimeInSeconds(),0.001);
        Assert.assertEquals(frame.getComment(),savedFrame.getComment());
        Assert.assertEquals(frame.getBlob().getFilename(),savedFrame.getBlob().getFilename());
    }

    @Test
    public void testGetAdapterUpdateSeveralFrame() throws Exception {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);

        Storyboard adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertEquals(0,adapter.size());
        List<Frame> frames = new ArrayList<>();
        frames.add(new Frame(blob,1.0,"test"));
        frames.add(new Frame(blob,2.0,"test2"));
        adapter.addAllFrames(frames);

        doc = session.saveDocument(doc);
        adapter = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertEquals(2,adapter.size());
    }

}
