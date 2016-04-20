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

package org.nuxeo.labs.video.storyboard.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitFilteringEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.labs.video.storyboard.service.StoryboardService;
import org.nuxeo.runtime.api.Framework;

import static org.nuxeo.ecm.core.api.CoreSession.ALLOW_VERSION_WRITE;
import static org.nuxeo.ecm.platform.video.VideoConstants.HAS_STORYBOARD_FACET;
import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_CHANGED_EVENT;


public class StoryboardListener implements PostCommitFilteringEventListener {

    public static final Log log = LogFactory.getLog(StoryboardListener.class);

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            if (VIDEO_CHANGED_EVENT.equals(event.getName())) {
                handleEvent(event);
            }
        }
    }

    public void handleEvent(Event event) {
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext docCtx = (DocumentEventContext) ctx;
        DocumentModel doc = docCtx.getSourceDocument();
        if (doc.hasFacet(HAS_STORYBOARD_FACET)) {
            StoryboardService service = Framework.getService(StoryboardService.class);
            String chainName = service.getDefaultChain();
            AutomationService as = Framework.getService(AutomationService.class);
            OperationContext octx = new OperationContext();
            octx.setInput(doc);
            octx.put("maxFrames",service.getMaxFrames());
            octx.put("minStepInSeconds",service.getMinStepInSeconds());
            octx.setCoreSession(doc.getCoreSession());
            OperationChain chain = new OperationChain("StoryboardListenerChain");
            chain.add(chainName);
            try {
                doc = (DocumentModel) as.run(octx, chain);
                CoreSession session = docCtx.getCoreSession();
                if (doc.isVersion()) {
                    doc.putContextData(ALLOW_VERSION_WRITE, Boolean.TRUE);
                }
                session.saveDocument(doc);
                session.save();
            } catch (OperationException e) {
                log.warn(e);
            }
        }
    }

    @Override
    public boolean acceptEvent(Event event) {
        return VIDEO_CHANGED_EVENT.equals(event.getName());
    }
}
