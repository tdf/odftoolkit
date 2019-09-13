/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.changes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import static org.odftoolkit.odfdom.changes.OperationConstants.*;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * The status of frames
 */
public class ShapeProperties extends CachedComponent {

    private static final long serialVersionUID = 1L;
    // *** FRAME PROPERTIES ***
    // Required as the component draw frame can only be delayed created,
    // After the first child has been parsed.. And should ONLY ONCE created!
    DrawFrameElement mDrawFrameElement = null;
    public List<Integer> mShapePosition = null;
    public Map<String, Object> mShapeHardFormatations = null;
    List<OdfElement> mFrameChildren = null;
    int mFrameChildrenNumber = 0;
    boolean mIsImageFrame = false;
    boolean mIsGroup = false;
    public Integer mVertOffsetMin = null;
    public Integer mHoriOffsetMin = null;
    public Integer mVertOffsetMax = null;
    public Integer mHoriOffsetMax = null;

    public DrawFrameElement getDrawFrameElement() {
        return mDrawFrameElement;
    }

//        private ShapeProperties() {
//        }
    public ShapeProperties(List<Integer> start, Map<String, Object> hardFormatations) {
        // Maps are being reused, for upcoming components, therefore the collections have to be cloned
        mShapePosition = new LinkedList<Integer>(start);
        if (hardFormatations != null) {
            mShapeHardFormatations = new HashMap<String, Object>();
            mShapeHardFormatations.putAll(hardFormatations);
            JSONObject originalDrawingProps = (JSONObject) hardFormatations.get("drawing");
            // Unfortunately the JSON lib being used, does not support deep cloning
            JSONObject newDrawingProps = new JSONObject();
            if (originalDrawingProps != null) {
                // copying hard
                for (String key : originalDrawingProps.keySet()) {
                    try {
                        newDrawingProps.put(key, originalDrawingProps.get(key));
                    } catch (JSONException ex) {
                        Logger.getLogger(ShapeProperties.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mShapeHardFormatations.put("drawing", newDrawingProps);
            }
            JSONObject originalImageProps = (JSONObject) hardFormatations.get("image");
            // Unfortunately the JSON lib being used, does not support deep cloning
            JSONObject newImageProps = new JSONObject();
            if (originalImageProps != null) {
                // copying hard
                for (String key : originalImageProps.keySet()) {
                    try {
                        newImageProps.put(key, originalImageProps.get(key));
                    } catch (JSONException ex) {
                        Logger.getLogger(ShapeProperties.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                mShapeHardFormatations.put("image", newImageProps);
            }
        }
    }

    /**
     * If the frame has one or more image elements this method dispatches an
     * operation for the first image
     *
     * @param desc description of the image
     */
    public void createShapeOperation(ChangesFileSaxHandler saxHandler, Stack<CachedComponent> mComponentStack, String desc, ShapeType shapeType, String context) {
        if (desc != null && !desc.isEmpty()) {
            JSONObject drawingProps = (JSONObject) this.mShapeHardFormatations.get("drawing");
            try {
                drawingProps.put("description", desc);
            } catch (JSONException ex) {
                Logger.getLogger(ShapeProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        CachedComponent parentComponent;
        if (isGroupShape()) {
            try {
                JSONObject drawingProps = (JSONObject) mShapeHardFormatations.get("drawing");
                if (mVertOffsetMin != null) {
                    drawingProps.put("anchorVertOffset", mVertOffsetMin);
                    if (mVertOffsetMax != null) {
                        drawingProps.put("height", mVertOffsetMax - mVertOffsetMin);
                    }
                }
                if (mHoriOffsetMin != null) {
                    drawingProps.put("anchorHorOffset", mHoriOffsetMin);
                    if (mHoriOffsetMax != null) {
                        drawingProps.put("width", mHoriOffsetMax - mHoriOffsetMin);
                    }
                }
            } catch (JSONException ex) {
                Logger.getLogger(ShapeProperties.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!mComponentStack.empty() && (parentComponent = mComponentStack.peek()) instanceof ShapeProperties && ((ShapeProperties) parentComponent).isGroupShape()) {
            JSONObject drawingProps = (JSONObject) mShapeHardFormatations.get("drawing");
            ((ShapeProperties) parentComponent).addMemberPosSize(drawingProps.opt("anchorHorOffset"), drawingProps.opt("anchorVertOffset"),
                    drawingProps.opt("width"), drawingProps.opt("height"));
        }
        if (getDrawFrameElement() != null) {
            Object fill = mShapeHardFormatations.get("fill");
            try {
                if (fill == null) {
                    fill = new JSONObject(1);
                    mShapeHardFormatations.put("fill", fill);
                    ((JSONObject) fill).put("color", "ffffff");
                }
            } catch (JSONException e) {
            }
        }
        switch (shapeType) {
            case ImageShape:
                saxHandler.cacheOperation(false, OperationConstants.IMAGE, this.mShapePosition, false, this.mShapeHardFormatations, context);
                break;
            case NormalShape: {
                if (mShapeHardFormatations.get(OPK_STYLE_ID) == null) {
                    Object lineObject = mShapeHardFormatations.get("line");
                    // shapes require a default line setting
                    try {
                        if (lineObject == null) {
                            lineObject = new JSONObject(1);
                            mShapeHardFormatations.put("line", lineObject);
                        }
                        if (!((JSONObject) lineObject).has("style")) {
                            ((JSONObject) lineObject).put(OPK_TYPE, "solid");
                            ((JSONObject) lineObject).put("width", 1);
                            ((JSONObject) lineObject).put("style", "solid");
                        }
                    } catch (JSONException e) {
                    }
                }
                saxHandler.cacheOperation(false, OperationConstants.SHAPE, this.mShapePosition, false, this.mShapeHardFormatations, context);
            }
            break;
            case GroupShape:
                saxHandler.cacheOperation(false, OperationConstants.SHAPE_GROUP, this.mShapePosition, false, this.mShapeHardFormatations, context);
                break;
        }
    }

    public void setDrawFrameElement(DrawFrameElement drawFrameElement) {
        mDrawFrameElement = drawFrameElement;
    }

    public Map<String, Object> getShapeHardFormatting() {
        return mShapeHardFormatations;
    }

    public boolean hasImageSibling() {
        return mIsImageFrame;
    }

    public void declareImage() {
        mIsImageFrame = true;
    }

    public void setGroupShape() {
        mIsGroup = true;
    }

    public boolean isGroupShape() {
        return mIsGroup;
    }

    public void addMemberPosSize(Object horiOffset, Object vertOffset, Object width, Object height) {
        if (vertOffset != null) {
            if (mVertOffsetMin == null) {
                mVertOffsetMin = (Integer) vertOffset;
            } else {
                mVertOffsetMin = Math.min(mVertOffsetMin, ((Integer) vertOffset).intValue());
            }
        }
        if (horiOffset != null) {
            if (mHoriOffsetMin == null) {
                mHoriOffsetMin = (Integer) horiOffset;
            } else {
                mHoriOffsetMin = Math.min(mHoriOffsetMin, ((Integer) horiOffset).intValue());
            }
        }
        if (width != null) {
            Integer maxHori = (Integer) width;
            if (horiOffset != null) {
                maxHori += (Integer) horiOffset;
            }
            if (mHoriOffsetMax == null) {
                mHoriOffsetMax = maxHori;
            } else {
                mHoriOffsetMax = Math.max(mHoriOffsetMax, maxHori);
            }
        }
        if (height != null) {
            Integer maxVert = (Integer) height;
            if (vertOffset != null) {
                maxVert += (Integer) vertOffset;
            }
            if (mVertOffsetMax == null) {
                mVertOffsetMax = maxVert;
            } else {
                mVertOffsetMax = Math.max(mVertOffsetMax, maxVert);
            }
        }
    }

    /**
     * In a frame there might be multiple objects, but only the first applicable
     * is shown. For instance, there are replacement images after an OLE object
     * *
     */
    public int incrementChildNumber() {
        return ++mFrameChildrenNumber;
    }

    public int decrementChildNumber() {
        return --mFrameChildrenNumber;
    }

    public int getChildNumber() {
        return mFrameChildrenNumber;
    }
    public Node mOwnNode = null;
    public String mDescription = null;
    public String mContext = null;
}
