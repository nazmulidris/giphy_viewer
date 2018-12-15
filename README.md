# App Description

This app is essentially a small [Giphy browser](https://giphy.com/). Here's the high-level
description.

1. The entry point for the app is an "infinitely scrolling" list of the trending GIFs on Giphy.

2. There's a search bar on top that will allow a user to search for GIFs. These results will replace
the trending GIFs.

3. When you click a GIF, it will take you to a fullscreen view of that GIF.

# References

- [Giphy developer page](https://developers.giphy.com/) 
- [Fresco](https://github.com/facebook/fresco) for GIF rendering

## Giphy API

- API Key: 
    - name: `giphy_viewer`
    - key: `mnVttajnx9Twmgp3vFbMQa3Gvn9Rv4Hg`

- How to paginate the responses in the API
    - [github](https://github.com/Giphy/GiphyAPI/issues/116)

- Android SDK is available
    - [Android SDK](https://github.com/Giphy/giphy-android-sdk-core)
    - [Media.java](http://tinyurl.com/ydac4992)
    - [GPHApiClient.java](http://tinyurl.com/ycvfz5mk)

## Greenrobot EventBus

- [Docs](https://github.com/greenrobot/EventBus)

## RecyclerView infinite scroll (request + result pagination)

- Infinite scrolling w/ RecyclerView
    - [Paginate library](https://github.com/MarkoMilos/Paginate)
    - [Source example](http://tinyurl.com/y8okxwta)

- Pull to refresh for RecyclerView
    - [SwipeRefreshLayout](http://tinyurl.com/y6u79co7)
    - [DAC](https://developer.android.com/training/swipe/)
    - [Tutorial](http://tinyurl.com/yc6eysty)
    
- SearchView and RecyclerView
    - [SO](https://stackoverflow.com/a/49064027/2085356)
    - [DAC](https://developer.android.com/training/search/setup)

## Fresco

- [Getting Started](https://frescolib.org/docs/index.html)
- [Animations](https://frescolib.org/docs/animations.html)
- [Customizations](http://tinyurl.com/yaldzkub)