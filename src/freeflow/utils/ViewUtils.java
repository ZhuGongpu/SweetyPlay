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
package freeflow.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import freeflow.core.FreeFlowItem;

import java.util.Map;

public class ViewUtils {
    public static FreeFlowItem getItemAt(Map<?, FreeFlowItem> frameDescriptors, int x, int y) {
        FreeFlowItem returnValue = null;

        for (FreeFlowItem item : frameDescriptors.values()) {
            if (item.frame.contains((int) x, (int) y)) {
                returnValue = item;
            }

        }
        return returnValue;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

}
