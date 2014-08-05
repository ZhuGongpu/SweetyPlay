package freeflow.animations;

import android.graphics.Rect;
import android.util.Pair;
import android.view.View;
import android.view.View.MeasureSpec;
import freeflow.core.FreeFlowContainer;
import freeflow.core.FreeFlowItem;
import freeflow.core.LayoutChangeset;

public class NoAnimationLayoutAnimator implements FreeFlowLayoutAnimator {

    private LayoutChangeset changes;

    @Override
    public LayoutChangeset getChangeSet() {
        return changes;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void animateChanges(LayoutChangeset changes,
                               FreeFlowContainer callback) {
        this.changes = changes;
        for (Pair<FreeFlowItem, Rect> item : changes.getMoved()) {
            Rect r = item.first.frame;
            View v = item.first.view;
            int wms = MeasureSpec.makeMeasureSpec(r.right - r.left, MeasureSpec.EXACTLY);
            int hms = MeasureSpec.makeMeasureSpec(r.bottom - r.top, MeasureSpec.EXACTLY);
            v.measure(wms, hms);
            v.layout(r.left, r.top, r.right, r.bottom);
        }
        callback.onLayoutChangeAnimationsCompleted(this);

    }

    @Override
    public boolean isRunning() {
        return false;
    }

}
