# Problem Definition

Had a great time talking with you! Here's the written description for the sample app that we briefly
talked about:

The app is essentially a small [Giphy browser](https://giphy.com/). Here's the high-level
description.

1. The entry point for the app is an "infinitely scrolling" list of the trending GIFs on Giphy.

2. There's a search bar on top that will allow a user to search for GIFs. These results will replace
the trending GIFs.

3. When you click a GIF, it will take you to a fullscreen view of that GIF.

Here's an APK that represents what an "ideal" solution would look like. I don't expect you to have a
complete clone. [APK](https://drive.google.com/open?id=1CERrNK_1E9bK5kOrlFD__nK4ReYHHa_P)

The goal for me is to get a feel for how you'd architect something like this -- the patterns you
use, your coding style, etc. So while there's plenty of opportunity to add visual "flare", I'd
prioritize getting the foundation well-built over any other bells and whistles.

You're more than welcome to use whatever libraries you want. Some things that may help:

- [Giphy developer page](https://developers.giphy.com/ (they do have an [Android
library](https://github.com/Giphy/giphy-android-sdk-core), but their API is also simple, so do
whatever you're comfortable with)

- [Fresco](https://github.com/facebook/fresco) or [Glide](https://github.com/bumptech/glide) for GIF
rendering

If you have any questions at all, feel free to message me via Signal at 610-393-5804.

Otherwise, push the code somewhere, and send me a link to the repo when you're done :)

# Solution

- [x] Create UI and stub out everything

    - [x] empty layout w/ recyclerview
        - [x] gradle imports for recyclerview, viewmodel, lifecycleobservable
        - [x] [androidx](https://developer.android.com/jetpack/androidx/migrate)
        - [x] layout Toolbar, SwipeRefreshLayout, RecyclerView

    - [x] put things in a viewmodel to handle rotation (last position and underlyingData)

    - [x] detect pull to refresh
        - [x] decide which mechanism to use [SwipeRefreshLayout](http://tinyurl.com/y6u79co7)

    - [x] detect overscroll down (to trigger data load)
        - [x] decide which mechanism to use [Paginate](https://github.com/MarkoMilos/Paginate)

- [ ] Replace stubs w/ real API calls
    - [x] wire pull to refresh into adapter
    - [x] wire infinite scroll into adapter
    - [x] integrate w/ trending endpoint
    - [x] figure out how to use Glide and RecyclerView ViewHolder binding
    - [x] open image into a full screen activity
    - [ ] wire search into adapter
    - [ ] integrate w/ search endpoint

# Research

## Giphy API

- API Key: 
    - name: `giphy_viewer`
    - key: `mnVttajnx9Twmgp3vFbMQa3Gvn9Rv4Hg`

- How to paginate the responses in the API
    - [github](https://github.com/Giphy/GiphyAPI/issues/116)

- Android SDK is available
    - [Android SDK](https://github.com/Giphy/giphy-android-sdk-core)

- Get results for trending endpoint
    - Determine which preview to use in RecyclerView
    - Determine which full size image to use in full screen activity showing the image

- Get results for search endpoint
    - Use same preview and full size items in response (as above)
    
## RecyclerView infinite scroll (request + result pagination)

- Figure out how to implement infinite scrolling in RecyclerView
    - [SO](http://tinyurl.com/yatxa4jj)    
    - [Paginate library](https://github.com/MarkoMilos/Paginate)
    - [Endless library](https://github.com/rockerhieu/rv-adapter-endless)

- Pull to refresh for RecyclerView
    - [SwipeRefreshLayout](http://tinyurl.com/y6u79co7)
    - [DAC](https://developer.android.com/training/swipe/)

- Alt impl of pull to refresh & infinite scroll
    - [overscroll-decor](https://github.com/nazmulidris/overscroll-decor)
    
- Tie the infinite scrolling behavior w/ Giphy API for both search and trending endpoints

- SearchView and RecyclerView
    - [SO](https://stackoverflow.com/a/49064027/2085356)
    - [DAC](https://developer.android.com/training/search/setup)

## Glide

- Figure out how to incorporate Glide into RecyclerView's ViewHolder