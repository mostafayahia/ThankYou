# ThankYou

The purpose of this app is watching embedded youtube videos for the songs from “Thank You Allah”
English album for Maher Zain singer. The embedded youtube videos enable the user to turn the
captions on and this will be helpful for Non Native English Speakers.

the app is already published on the store:

    https://play.google.com/store/apps/details?id=nd801project.elmasry.thankyou

### Important:
- You have to set your youtube developer key in strings.xml file before running the app, please make sure it works without errors by using the following link: https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=PLoagsPg26SY7YdytkX5rJhX3aFmbvaZRM&key=API-KEY
- You have to set ga-trackingId in xml/global-tracker.xml for google analytics.

#### Regrading the app widget:
- If there is no song watched, the app widget will display the first song in the list by default.
- If there is no favorite songs, the widget will display the last watched song. Other wise the app will display one of favorite songs.
- The widget will be automaitcally updated every 24 hours.

##### More details:
- To get more details about the project, you can visit: https://github.com/mostafayahia/Capstone-Project

## LICENSE

Copyright (C) 2018 Yahia H. El-Tayeb

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

