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


public class Play {

    private String activityPhotoURL = "";
    private String tittle = "";
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    /**
     * TODO
     * 添加其他信息
     */


    public String getActivityPhotoURL() {
        return activityPhotoURL;
    }

    public void setActivityPhotoURL(String activityPhotoURL) {
        this.activityPhotoURL = activityPhotoURL;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Play)) return false;
        Play oShot = (Play) other;
        return oShot.activityPhotoURL.equals(activityPhotoURL);
//        && (oShot.id == this.id);
    }

}
