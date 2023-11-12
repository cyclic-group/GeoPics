# GeoPics

## App Architecture
The app runs in one activity which hosts several fragments. The navigation graph can be found in
/res/navigation directory. Fragment states are saved using associated view models, while persistent
states are saved under a private local directory.

## Google Maps
Associated code can be found in GoogleMapFragment, GoogleMapViewModel, and fragment_google_map.xml.
When created, the fragment would initialize a background worker that continuously tracks the position
of the user and draws the route on the map.

## Authentification
Associated code can be found in LoginFragment, LoginViewModel, and fragment_login.xml. The app uses
email to identify each user, and each user can only see their own posts.

## Photo Uploading
Associated code can be found in UploadPhotoFragment, UploadPhotoViewModel, and upload_photo_fragment.xml.
Users can either take photo with their cameras or pick photo from the image gallery.

# Nearby Place
Associated code can be found in PlaceListAdapter, PlacesLoader, PhotoScrollingFragment, fragment_photo_scrolling.xml,
and place_item. When user clicks their position mark, they will be navigated to the recyclerView which lists
nearby places.

# API Key
The API key should be stored in local.properties and named GOOGLE_MAPS_API_KEY.