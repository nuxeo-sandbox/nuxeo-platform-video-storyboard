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

package org.nuxeo.ecm.platform.video.storyboard.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.event.impl.EventBundleImpl;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.video.storyboard.adapter.StoryboardAdapter;
import org.nuxeo.ecm.platform.video.storyboard.listener.StoryboardListener;
import org.nuxeo.ecm.platform.video.storyboard.service.Storyboard;
import org.nuxeo.ecm.platform.video.storyboard.service.StoryboardService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_CHANGED_EVENT;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.ecm.automation.scripting",
        "org.nuxeo.ecm.platform.video.storyboard",
        "org.nuxeo.ecm.platform.commandline.executor",
        "org.nuxeo.ecm.platform.video.core",
        "org.nuxeo.ecm.platform.video.convert"
})
@LocalDeploy({
        "nuxeo-vision-core:OSGI-INF/disable-conversion-listener-contrib.xml"
})
public class StoryboardListenerTest {

    @Inject
    CoreSession session;

    @Inject
    StoryboardService storyboardService;

    @Test
    public void testListenerChain() throws IOException, OperationException {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);

        AutomationService as = Framework.getService(AutomationService.class);
        OperationContext ctx = new OperationContext();
        ctx.setInput(doc);
        ctx.put("maxFrames",10);
        ctx.put("minStepInSeconds",1);
        ctx.setCoreSession(session);
        OperationChain chain = new OperationChain("TestChain");
        chain.add(storyboardService.getDefaultChain());
        doc = (DocumentModel) as.run(ctx, chain);

        Storyboard storyboard = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertTrue(storyboard.size()>0);

    }

    @Test
    public void testListener() throws IOException, OperationException {
        DocumentModel doc = session.createDocumentModel("/", "Video", "Video");
        File file = new File(getClass().getResource("/files/TourEiffel.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);

        EventContextImpl evctx = new DocumentEventContext(session, session.getPrincipal(),doc);
        Event event = evctx.newEvent(VIDEO_CHANGED_EVENT);
        EventBundle bundle = new EventBundleImpl();
        bundle.push(event);

        StoryboardListener listener = new StoryboardListener();
        listener.handleEvent(bundle);

        doc = session.getDocument(doc.getRef());
        Storyboard storyboard = doc.getAdapter(StoryboardAdapter.class);
        Assert.assertTrue(storyboard.size()>0);
    }

}
