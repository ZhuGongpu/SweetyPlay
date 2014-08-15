/*******************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
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
 ******************************************************************************/
package app.view.play_list;

import android.graphics.Rect;
import android.util.Log;
import freeflow.core.FreeFlowItem;
import freeflow.core.Section;
import freeflow.layouts.FreeFlowLayout;
import freeflow.layouts.FreeFlowLayoutBase;
import freeflow.utils.ViewUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class PlayListLayout extends FreeFlowLayoutBase implements FreeFlowLayout {

    private static final String TAG = "ArtbookLayout";

    private int largeItemSide;
    private int regularItemSide;
    private HashMap<Object, FreeFlowItem> map;
    private Section s;

    @Override
    public void setDimensions(int measuredWidth, int measuredHeight) {
        super.setDimensions(measuredWidth, measuredHeight);
        largeItemSide = measuredWidth / 2;
        regularItemSide = measuredWidth / 4;
    }

    @Override
    public void prepareLayout() {
        Log.d(TAG, "prepare layout!!!");
        map = new HashMap<Object, FreeFlowItem>();
        s = itemsAdapter.getSection(0);
        int rowIndex;
        Log.d(TAG, "prepare layout for: " + s.getDataCount());
        for (int i = 0; i < s.getDataCount(); i++) {
            rowIndex = i / 5;

            FreeFlowItem freeFlowItem = new FreeFlowItem();
            freeFlowItem.isHeader = false;
            freeFlowItem.itemIndex = i;
            freeFlowItem.itemSection = 0;
            freeFlowItem.data = s.getDataAtIndex(i);

            Rect rect = new Rect();

            switch (i % 5) {
                case (0):
                    rect.left = 0;
                    rect.top = rowIndex * largeItemSide;
                    rect.right = largeItemSide;
                    rect.bottom = rect.top + largeItemSide;

                    if (rowIndex % 2 != 0) {
                        rect.offset(largeItemSide, 0);
                    }
                    break;
                case (1):
                    rect.left = largeItemSide;
                    rect.right = largeItemSide + regularItemSide;
                    rect.top = rowIndex * largeItemSide;
                    rect.bottom = rect.top + regularItemSide;

                    if (rowIndex % 2 != 0) {
                        rect.offset(-largeItemSide, 0);
                    }
                    break;
                case (2):
                    rect.left = 3 * regularItemSide;
                    rect.right = width;
                    rect.top = rowIndex * largeItemSide;
                    rect.bottom = rect.top + regularItemSide;

                    if (rowIndex % 2 != 0) {
                        rect.offset(-largeItemSide, 0);
                    }
                    break;
                case (3):
                    rect.left = largeItemSide;
                    rect.right = largeItemSide + regularItemSide;
                    rect.top = rowIndex * largeItemSide + regularItemSide;
                    rect.bottom = rect.top + regularItemSide;
                    if (rowIndex % 2 != 0) {
                        rect.offset(-largeItemSide, 0);
                    }
                    break;
                case (4):
                    rect.left = 3 * regularItemSide;
                    rect.right = width;
                    rect.top = rowIndex * largeItemSide + regularItemSide;
                    rect.bottom = rect.top + regularItemSide;
                    if (rowIndex % 2 != 0) {
                        rect.offset(-largeItemSide, 0);
                    }
                    break;
                default:
                    break;
            }
            freeFlowItem.frame = rect;
            map.put(s.getDataAtIndex(i), freeFlowItem);
        }
    }

    @Override
    public HashMap<Object, FreeFlowItem> getItemProxies(
            int viewPortLeft, int viewPortTop) {

        Rect viewport = new Rect(viewPortLeft,
                viewPortTop,
                viewPortLeft + width,
                viewPortTop + height);

        //Log.d(TAG, "Viewport: "+viewPortLeft+", "+viewPortTop+", "+viewport.width()+","+viewport.height());
        HashMap<Object, FreeFlowItem> ret = new HashMap<Object, FreeFlowItem>();

        Iterator<Entry<Object, FreeFlowItem>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, FreeFlowItem> pairs = it.next();
            FreeFlowItem p = pairs.getValue();
            if (Rect.intersects(p.frame, viewport)) {
                ret.put(pairs.getKey(), p);
            }
        }
        return ret;

    }

    @Override
    public FreeFlowItem getFreeFlowItemForItem(Object item) {
        Log.d(TAG, " returing item: " + map.get(item));
        return map.get(item);
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return s.getDataCount() / 5 * largeItemSide;
    }

    @Override
    public FreeFlowItem getItemAt(float x, float y) {
        return ViewUtils.getItemAt(map, (int) x, (int) y);
    }

    @Override
    public void setLayoutParams(FreeFlowLayoutParams params) {


    }

    @Override
    public boolean verticalScrollEnabled() {
        return true;
    }

    @Override
    public boolean horizontalScrollEnabled() {
        return false;
    }
}
