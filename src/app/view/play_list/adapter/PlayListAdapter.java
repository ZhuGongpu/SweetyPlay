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
package app.view.play_list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.view.login.R;
import avos.models.PlayEntity;
import com.squareup.picasso.Picasso;
import freeflow.core.FreeFlowItem;
import freeflow.core.Section;
import freeflow.core.SectionedAdapter;

import java.util.List;

public class PlayListAdapter implements SectionedAdapter {

    public static final String TAG = "PlayListAdapter";

    private Context context;
    private Section section;

    private int[] colors = new int[]{0xcc152431, 0xff264C58, 0xffF5C543,
            0xffE0952C, 0xff9A5325, 0xaaE0952C, 0xaa9A5325, 0xaa152431,
            0xaa264C58, 0xaaF5C543, 0x44264C58, 0x44F5C543, 0x44152431};

    private boolean hideImages = false;

    public PlayListAdapter(Context context) {
        this.context = context;
        section = new Section();
        section.setSectionTitle("Pics");
    }

    /**
     * 清除所有数据
     */
    public void clearData() {
        section.getData().clear();
    }

    public void update(List<PlayEntity> playList) {
        for (PlayEntity entity : playList) {
            section.getData().add(entity);
        }
    }

    @Override
    public long getItemId(int section, int position) {
        return section * 1000 + position;
    }

    @Override
    public View getItemView(int sectionIndex, int position, View convertView,
                            ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.playlist_item_layout, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.pic);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        if (hideImages) {
            int idx = position % colors.length;
            image.setBackgroundColor(colors[idx]);
        } else {

            PlayEntity play = (PlayEntity) this.section.getData().get(position);
            title.setText(play.getTitle());
            Picasso.with(context)
                    .load(play.getThumbnailUrl())
                    .into(image);
        }
        return convertView;
    }

    @Override
    public View getHeaderViewForSection(int section, View convertView,
                                        ViewGroup parent) {
        return null;
    }

    @Override
    public int getNumberOfSections() {
        if (section.getData().size() == 0) return 0;
        return 1;
    }

    @Override
    public Section getSection(int index) {
        return section;
    }

    @Override
    public Class[] getViewTypes() {
        return new Class[]{LinearLayout.class};
    }

    @Override
    public Class getViewType(FreeFlowItem proxy) {
        return LinearLayout.class;
    }

    @Override
    public boolean shouldDisplaySectionHeaders() {
        return false;
    }

}
