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
package app.models;

import android.os.AsyncTask;
import app.view.play.PlayFragment;
import avos.AVOSWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.List;

public class PlayListFetch {

    public static final String TAG = "DribbleFetch";


    public void load(final PlayFragment caller) {

        //TODO 获取数据  可以改为采用avos api直接获取数据，然后修改caller.onDataLoaded

        new AsyncTask<Void, Void, Void>() {
            private List<Play> plays = null;

            PlayList playList = new PlayList();

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    loadAVOSData();
                } catch (AVException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                playList.setPlays(plays);
                caller.onDataLoaded(playList);
            }

            /**
             * 加载全部数据
             *
             * @throws com.avos.avoscloud.AVException
             */
            void loadAVOSData() throws AVException {

                if (plays == null)
                    plays = new ArrayList<Play>(10);

                AVQuery<AVObject> query = new AVQuery<AVObject>("Play");

                //query.setLimit(10);

                List<AVObject> result = query.find();

                for (AVObject object : result) {
                    AVFile activityPhoto = object.getAVFile("ActivityPhoto");

                    //TODO 设置play信息

                    Play play = new Play();
                    play.setActivityPhotoURL(AVOSWrapper.getThubnailUrl(activityPhoto, 200, 200));
                    play.setTittle(object.getString("Title"));
                    play.setDescription(object.getString("Description"));

                    plays.add(play);
                }
            }

        }.execute();
    }
}
