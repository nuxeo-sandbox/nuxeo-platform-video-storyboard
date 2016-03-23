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

package org.nuxeo.ecm.platform.video.storyboard.automation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.video.VideoDocument;
import org.nuxeo.ecm.platform.video.storyboard.adapter.StoryboardAdapter;
import org.nuxeo.ecm.platform.video.storyboard.service.Storyboard;
import org.nuxeo.ecm.platform.video.storyboard.service.StoryboardService;

/**
 *
 */
@Operation(
        id= StoryboardOp.ID,
        category=Constants.CAT_BLOB,
        label="Generate a storyboard for the input video document",
        description="Generate a storyboard for the input video document")
public class StoryboardOp {

    public static final String ID = "Storyboard";

    private static final Log log = LogFactory.getLog(StoryboardOp.class);

    @Context
    protected OperationContext ctx;

    @Context
    protected StoryboardService storyboardService;

    @Param(
            name = "size",
            description= "The maximum size of the storyboard")
    protected int size = 10;

    @Param(
            name = "save",
            description= "Save modification made to the input document")
    protected boolean save = false;


    @OperationMethod
    public DocumentModel run(DocumentModel doc) {
        VideoDocument videoDoc = doc.getAdapter(VideoDocument.class);
        if (videoDoc==null) return doc;
        try {
            Storyboard storyboard = storyboardService.generateStoryboard(videoDoc.getVideo(),size);
            StoryboardAdapter adapter = doc.getAdapter(StoryboardAdapter.class);
            adapter.addAllFrames(storyboard.getFrames());
        } catch (Exception e) {
            log.error(e);
        }
        if (save) {
            doc = ctx.getCoreSession().saveDocument(doc);
        }
        return doc;
    }

}