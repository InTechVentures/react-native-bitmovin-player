package com.takeoffmediareactnativebitmovinplayer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.bitmovin.analytics.BitmovinAnalyticsConfig;
import com.bitmovin.analytics.bitmovin.player.BitmovinPlayerCollector;
import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.Player;
import com.bitmovin.player.api.PlayerConfig;
import com.bitmovin.player.api.drm.WidevineConfig;
import com.bitmovin.player.api.event.PlayerEvent;
import com.bitmovin.player.api.media.subtitle.SubtitleTrack;
import com.bitmovin.player.api.media.thumbnail.ThumbnailTrack;
import com.bitmovin.player.api.source.Source;
import com.bitmovin.player.api.source.SourceConfig;
import com.bitmovin.player.api.source.SourceType;

import com.bitmovin.player.api.ui.FullscreenHandler;
import com.bitmovin.player.api.ui.StyleConfig;
import com.bitmovin.player.ui.CustomMessageHandler;
import com.bitmovin.player.api.event.EventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.net.CookiePolicy;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class ReactNativeBitmovinPlayerManager extends SimpleViewManager<PlayerView> implements FullscreenHandler, LifecycleEventListener {

  public static final String REACT_CLASS = "ReactNativeBitmovinPlayer";


  private SourceConfig sourceConfig;
  private BitmovinPlayerCollector analyticsCollector;
  private PlayerView _playerView;
  private Player _player;
  private boolean _fullscreen;
  private boolean _PiP;
  private ThemedReactContext _reactContext;
  private Integer heartbeat = 30;
  private Double offset = 0.0;
  private boolean nextCallback = false;
  private boolean customSeek = false;
  private ReadableMap configuration = null;
  private final PlayerConfig playerConfig = new PlayerConfig();
  private HashMap<String, String> metaDataMap = new HashMap<String, String>();
  private boolean playerShouldPause = true;

  private static final CookieManager DEFAULT_COOKIE_MANAGER;
  static {
    DEFAULT_COOKIE_MANAGER = new CookieManager();
    /** Here we can set a cookie policy.
     * Either `ACCEPT_ALL` or `ACCEPT_ORIGINAL_SERVER`, both should do the trick in this case.
     **/
    DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
  }

  @NotNull
  @Override
  public String getName() {
      return REACT_CLASS;
  }

  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder()
      .put(
        "onReady",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onReady")
        )
      )
      .put(
        "onChromecast",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onChromecast")
        )
      )
      .put(
        "onEvent",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onEvent")
        )
      )
      .put(
        "onForward",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onForward")
        )
      )
      .put(
        "onRewind",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onRewind")
        )
      )
      .put(
        "onPlay",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPlay")
        )
      )
      .put(
        "onPause",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPause")
        )
      )
      .put(
        "onTimeChanged",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onTimeChanged")
        )
      )
      .put(
        "onStallStarted",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onStallStarted")
        )
      )
      .put(
        "onStallEnded",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onStallEnded")
        )
      )
      .put(
        "onPlaybackFinished",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPlaybackFinished")
        )
      )
      .put(
        "onRenderFirstFrame",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onRenderFirstFrame")
        )
      )
      .put(
        "onError",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "_onPlayerError")
        )
      )
      .put(
        "onMuted",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onMuted")
        )
      )
      .put(
        "onUnmuted",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onUnmuted")
        )
      )
      .put(
        "onPiPEnter",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPiPAvailabilityChanged")
        )
      )
      .put(
        "onPiPEnter",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPiPEnter")
        )
      )
      .put(
        "onPiPEnter",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onPiPExit")
        )
      )
      .put(
        "onSeek",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onSeek")
        )
      )
      .put(
        "onSeeked",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onSeeked")
        )
      )
      .put(
        "onFullscreenEnter",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "onFullscreenEnter")
        )
      )
      .put(
        "_onFullscreenExit",
        MapBuilder.of(
          "phasedRegistrationNames",
          MapBuilder.of("bubbled", "_onFullscreenExit")
        )
      )
      .build();
    }

  // Create a custom javascriptInterface object which takes over the Bitmovin Web UI -> native calls
  Object javascriptInterface = new Object() {
    @JavascriptInterface
    public void closePlayerAsync(String data) {
      WritableMap map = Arguments.createMap();
      map.putString("message", "closePlayer");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      if (analyticsCollector != null) {
        analyticsCollector.detachPlayer();
      }
      try {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onEvent",
          map);
      } catch (Exception e) {
        throw new ClassCastException(String.format("Cannot onEvent closePlater error message: %s", e.getMessage()));
      }
      removeListeners();
      _player.unload();
      _player.destroy();
    }
    @JavascriptInterface
    public void nextEpisodeAsync(String data) {
      WritableMap map = Arguments.createMap();
      map.putString("message", "nextEpisode");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      if (analyticsCollector != null) {
        analyticsCollector.detachPlayer();
      }
      try {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onEvent",
          map);
      } catch (Exception e) {
        throw new ClassCastException(String.format("Cannot onEvent nextEpisode error message: %s", e.getMessage()));
      }
      removeListeners();
      _player.unload();
      _player.destroy();
    }
    @JavascriptInterface
    public void chromecastAsync(String data) {
      WritableMap map = Arguments.createMap();
      map.putString("message", "chromecast");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      try {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onChromecast",
          map);
      } catch (Exception e) {
        throw new ClassCastException(String.format("Cannot onChromecast error message: %s", e.getMessage()));
      }
    }
    @JavascriptInterface
    public void forwardButtonAsync(String data) {
      WritableMap map = Arguments.createMap();
      map.putString("message", "forwardButton");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      _player.seek(_player.getCurrentTime() + 10);

      customSeek = true;
      try {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onForward",
          map);
      } catch (Exception e) {
        throw new ClassCastException(String.format("Cannot onForward error message: %s", e.getMessage()));
      }
    }
    @JavascriptInterface
    public void rewindButtonAsync(String data) {
      WritableMap map = Arguments.createMap();
      map.putString("message", "rewindButton");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      _player.seek(_player.getCurrentTime() - 10);
      customSeek = true;
      try {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onRewind",
          map);
      } catch (Exception e) {
        throw new ClassCastException(String.format("Cannot onRewind error message: %s", e.getMessage()));
      }
    }
  };

  // Setup CustomMessageHandler for communication with Bitmovin Web UI
  private final CustomMessageHandler customMessageHandler = new CustomMessageHandler(javascriptInterface);

  @NotNull
  @Override
  public PlayerView createViewInstance(@NotNull ThemedReactContext context) {
    _reactContext = context;
    try {
      ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
      String BITMOVIN_CSS = appInfo.metaData.getString("BITMOVIN_PLAYER_CSS");
      String BITMOVIN_JS = appInfo.metaData.getString("BITMOVIN_PLAYER_JS");

      if (!BITMOVIN_CSS.equals("") && !BITMOVIN_JS.equals("")) {
        StyleConfig styleConfig = new StyleConfig();
        styleConfig.setPlayerUiCss(BITMOVIN_CSS);
        styleConfig.setPlayerUiJs(BITMOVIN_JS);
        playerConfig.setStyleConfig(styleConfig);

      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    _player = Player.create(context, playerConfig);
    _playerView = new PlayerView(context, _player);
    _playerView.setCustomMessageHandler(customMessageHandler);
    _fullscreen = false;
    setListeners();
    nextCallback = false;

    return _playerView;

  }


  @Override
  public void onDropViewInstance(@NotNull PlayerView view) {
    removeListeners();
    _playerView.onDestroy();
    super.onDropViewInstance(view);
    _player = null;
    _playerView = null;
  }

  @ReactProp(name = "analytics")
  public void setAnalytics(PlayerView view, ReadableMap analytics) {
    String title = "";
    String videoId = "";
    String userId = "";
    String cdnProvider = "";
    String customData1 = "";
    String customData2 = "";
    String customData3 = "";
    String customData4 = "";
    if (analytics != null && analytics.getString("title") != null) {
      title = analytics.getString("title");
    }
    if (analytics != null && analytics.getString("videoId") != null) {
      videoId = analytics.getString("videoId");
    }
    if (analytics != null && analytics.getString("userId") != null) {
      userId = analytics.getString("userId");
    }
    if (analytics != null && analytics.getString("cdnProvider") != null) {
      cdnProvider = analytics.getString("cdnProvider");
    }
    if (analytics != null && analytics.getString("customData1") != null) {
      customData1 = analytics.getString("customData1");
    }
    if (analytics != null && analytics.getString("customData2") != null) {
      customData2 = analytics.getString("customData2");
    }
    if (analytics != null && analytics.getString("customData3") != null) {
      customData3 = analytics.getString("customData3");
    }
    if (analytics != null && analytics.getString("customData4") != null) {
      customData4 = analytics.getString("customData4");
    }
    try {
      ApplicationInfo appInfo = _reactContext.getPackageManager().getApplicationInfo(_reactContext.getPackageName(),PackageManager.GET_META_DATA);
      String BITMOVIN_ANALYTICS_LICENSE_KEY = appInfo.metaData.getString("BITMOVIN_ANALYTICS_LICENSE_KEY");

      if (
        analytics != null && BITMOVIN_ANALYTICS_LICENSE_KEY != null &&
          !BITMOVIN_ANALYTICS_LICENSE_KEY.equals("")
      ) {
      // Create a BitmovinAnalyticsConfig using your Bitmovin analytics license key and (optionally) your Bitmovin Player Key
      BitmovinAnalyticsConfig bitmovinAnalyticsConfig = new BitmovinAnalyticsConfig(BITMOVIN_ANALYTICS_LICENSE_KEY);
      bitmovinAnalyticsConfig.setVideoId(videoId);
      bitmovinAnalyticsConfig.setTitle(title);
      bitmovinAnalyticsConfig.setCustomUserId(userId);
      bitmovinAnalyticsConfig.setCdnProvider(cdnProvider);
      bitmovinAnalyticsConfig.setCustomData1(customData1);
      bitmovinAnalyticsConfig.setCustomData2(customData2);
      bitmovinAnalyticsConfig.setCustomData3(customData3);
      bitmovinAnalyticsConfig.setCustomData4(customData4);

      // Create a BitmovinPlayerCollector object using the BitmovinAnalyitcsConfig you just created
      analyticsCollector = new BitmovinPlayerCollector(bitmovinAnalyticsConfig, _reactContext);

      // Attach your player instance
      analyticsCollector.attachPlayer(_player);

      } else {
        throw new ClassCastException("Cannot connect Analytics, add you license key.");
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }


  @ReactProp(name = "configuration")
  public void setConfiguration(PlayerView view, ReadableMap config) throws MalformedURLException, URISyntaxException {
    configuration = config;
    String advisory;
    ReadableMap cookies;
    boolean hasNextEpisode;

    if (config != null && config.getString("url") != null) {

      hasNextEpisode = config.getBoolean("hasNextEpisode");

      if (config.hasKey("cookies")) {
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
          CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        
        URL cookieUrl = new URL(config.getString("url"));
        URI domain = new URI(cookieUrl.getProtocol() + "://" + cookieUrl.getHost());

        ArrayList<HttpCookie> cookiesToAdd = new ArrayList();
        ReadableMap cookiesMap = config.getMap("cookies");
        ReadableMapKeySetIterator iterator = cookiesMap.keySetIterator();
        while (iterator.hasNextKey()) {
          String key = iterator.nextKey();

          HttpCookie cookie = new HttpCookie(key, cookiesMap.getString(key));
          cookie.setVersion(0);

          cookiesToAdd.add(cookie);
        }
        

        CookieStore cookieStore = DEFAULT_COOKIE_MANAGER.getCookieStore();
        for (HttpCookie cookie : cookiesToAdd) {

          cookieStore.add(domain, cookie);
        }
      }

      if (config.hasKey("hearbeat")) {
        heartbeat = (int)config.getDouble("hearbeat");
      }

      sourceConfig = new SourceConfig(
        Objects.requireNonNull(config.getString("url")),
        SourceType.Dash
      );

      if (config.getMap("advisory") != null) {
        metaDataMap.put("hasNextEpisode", hasNextEpisode ? "true" : "false");
        try {
          advisory = Objects.requireNonNull(config.getMap("advisory")).toString();
          metaDataMap.put("advisory", new JSONObject(advisory).toString());
        } catch (JSONException e) {
          e.printStackTrace();
        }

        sourceConfig.setMetadata(metaDataMap);
      }

      if (config.getString("title") != null) {
        sourceConfig.setTitle(Objects.requireNonNull(config.getString("title")));
      }

      if (config.getString("subtitle") != null) {
        sourceConfig.setDescription(Objects.requireNonNull(config.getString("subtitle")));
      }

      if (config.getString("thumbnails") != null) {
        ThumbnailTrack thumbnailTrack = new ThumbnailTrack(Objects.requireNonNull(config.getString("thumbnails")));
        sourceConfig.setThumbnailTrack(thumbnailTrack);
      }

      if (config.getString("poster") != null) {
        sourceConfig.setPosterImage(Objects.requireNonNull(config.getString("poster")), false);
      }

      if (config.hasKey("subtitles")) {
        ReadableType type = config.getType("subtitles");
        if (type == ReadableType.String) {
          if (config.getString("subtitles") != null) {
            SubtitleTrack subtitleTrack = new SubtitleTrack(config.getString("subtitles"), "text/vtt", "en", "en", false, "en");
            sourceConfig.addSubtitleTrack(subtitleTrack);
          }
        }
        if (type == ReadableType.Array) {
          ReadableArray subtitles = config.getArray("subtitles");
          if (subtitles != null) {
            for (int i = 0; i < subtitles.size(); i++) {
              ReadableMap subtitle = subtitles.getMap(i);
              assert subtitle != null;
              SubtitleTrack subtitleTrack = new SubtitleTrack(
                subtitle.getString("href"),
                "text/vtt",
                subtitle.getString("label"),
                Objects.requireNonNull(subtitle.getString("language")),
                false,
                subtitle.getString("language")
              );
              sourceConfig.addSubtitleTrack(subtitleTrack);
            }

          }
        }
      }


      if (config.hasKey("startOffset")) {
        sourceConfig.getOptions().setStartOffset(config.getDouble("startOffset"));
      }

      if (config.getMap("drm") != null) {
        String drmConf = Objects.requireNonNull(config.getMap("drm")).toString();
        try {
          JSONObject drmMapObj = new JSONObject(drmConf);
          String drmNativeMap = drmMapObj.getJSONObject("NativeMap").toString();
          JSONObject drm = new JSONObject(drmNativeMap);
          if (drm.getString("isDrmEn").equals("true")) {
            String licenseUrl = drm.getString("licenseUrl");
            String drmHeader = drm.getString("header");
            String drmToken = drm.getString("token");
            HashMap<String, String> drmWVConfigHeader = new HashMap<>();
            drmWVConfigHeader.put(drmHeader, drmToken);
            WidevineConfig widevineConfig = new WidevineConfig(licenseUrl);
            widevineConfig.setHttpHeaders(drmWVConfigHeader);
            sourceConfig.setDrmConfig(widevineConfig);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

      Source source = Source.create(sourceConfig);

      _player.load(source);

    }
  }

  @ReactProp(name = "hasChromecast")
  public void setChromecast(PlayerView view, Boolean hasChromecast) {
    metaDataMap.put("hasChromecast", hasChromecast ? "true" : "false");
    if(sourceConfig != null) {
      sourceConfig.setMetadata(metaDataMap);
    }
  }

  @Override
  public void onResume() {}
  @Override
  public void onPause() {}
  @Override
  public void onDestroy() {}
  @Override
  public void onFullscreenRequested() {
    _fullscreen = true;
  }
  @Override
  public void onFullscreenExitRequested() {
    _fullscreen = false;
  }
  @Override
  public void onHostResume() {
    _playerView.onResume();
  }
  @Override
  public void onHostPause() {
    _playerView.onPause();
  }
  @Override
  public void onHostDestroy() {
    _playerView.onDestroy();
  }
  @Override
  public boolean isFullscreen() {
    return _fullscreen;
  }

  private void setListeners() {
    _player.on(PlayerEvent.Ready.class, onReadyListener);
    _player.on(PlayerEvent.Play.class, onPlayListener);
    _player.on(PlayerEvent.Paused.class, onPausedListener);
    _player.on(PlayerEvent.TimeChanged.class, onTimeChangedListener);
    _player.on(PlayerEvent.PlaybackFinished.class, onPlaybackFinishedistener);
    _player.on(PlayerEvent.RenderFirstFrame.class, onRenderFirstFrameListener);
    _player.on(PlayerEvent.Error.class, onErrorListener);
    _player.on(PlayerEvent.Muted.class, onMutedListener);
    _player.on(PlayerEvent.Unmuted.class, onUnmutedListener);
    _player.on(PlayerEvent.Seek.class, onSeekListener);
    _player.on(PlayerEvent.Seeked.class, onSeekedListener);
    _player.on(PlayerEvent.FullscreenEnter.class, onFullscreenEnterListener);
    _player.on(PlayerEvent.FullscreenExit.class, onFullscreenExitListener);
  }

  private void removeListeners() {
    _player.off(onReadyListener);
    _player.off(onPlayListener);
    _player.off(onPausedListener);
    _player.off(onTimeChangedListener);
    _player.off(onPlaybackFinishedistener);
    _player.off(onRenderFirstFrameListener);
    _player.off(onErrorListener);
    _player.off(onMutedListener);
    _player.off(onUnmutedListener);
    _player.off(onSeekListener);
    _player.off(onSeekedListener);
    _player.off(onFullscreenEnterListener);
    _player.off(onFullscreenExitListener);
  }

  private final EventListener<PlayerEvent.Ready> onReadyListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      map.putString("message", "load");
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onReady",
        map);
    }
  };
  private final EventListener<PlayerEvent.Play> onPlayListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      map.putString("message", "play");
      map.putDouble("time", event.getTime());
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onPlay",
        map);
    }
  };
  private final EventListener<PlayerEvent.Paused> onPausedListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      map.putString("message", "pause");
      map.putDouble("time", event.getTime());
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onPause",
        map);
    }
  };
  private final EventListener<PlayerEvent.TimeChanged> onTimeChangedListener = event -> {
    if(_player != null && _player.getSource() != null){
      // next
      if (configuration != null && configuration.hasKey("nextPlayback") && event.getTime() != 0.0) {
        if (event.getTime() <= _player.getDuration() - (configuration.getDouble("nextPlayback")) && nextCallback) {
          nextCallback = false;
        }
        if (event.getTime() > _player.getDuration() - (configuration.getDouble("nextPlayback")) && !nextCallback) {
          nextCallback = true;
          WritableMap map = Arguments.createMap();
          map.putString("message", "next");
          _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
            _playerView.getId(),
            "onEvent",
            map);
        }
      }
      // save
      if((event.getTime() > (offset + heartbeat) || event.getTime() < (offset - heartbeat)) && event.getTime() < (_player.getDuration())) {
        offset = event.getTime();
        WritableMap map = Arguments.createMap();
        map.putString("message", "save");
        map.putString("time", String.valueOf(_player.getCurrentTime()));
        map.putString("volume", String.valueOf(_player.getVolume()));
        map.putString("duration", String.valueOf(_player.getDuration()));
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onEvent",
          map);
      }
    }
  };
  private final EventListener<PlayerEvent.PlaybackFinished> onPlaybackFinishedistener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onPlaybackFinished",
        map);
    }
  };
  private final EventListener<PlayerEvent.RenderFirstFrame> onRenderFirstFrameListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onRenderFirstFrame",
        map);
    }
  };
  private final EventListener<PlayerEvent.Error> onErrorListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      WritableMap errorMap = Arguments.createMap();
      errorMap.putInt("code", Integer.parseInt(String.valueOf(event.getCode())));
      errorMap.putString("message", event.getMessage());
      map.putMap("error", errorMap);
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onError",
        map);
    }
  };
  private final EventListener<PlayerEvent.Muted> onMutedListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onMuted",
        map);
    }
  };
  private final EventListener<PlayerEvent.Unmuted> onUnmutedListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onUnmuted",
        map);
    }
  };
  private final EventListener<PlayerEvent.Seek> onSeekListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      map.putString("message", "seek");
      map.putString("time", String.valueOf(_player.getCurrentTime()));
      map.putDouble("position", event.getTimestamp());
      map.putString("volume", String.valueOf(_player.getVolume()));
      map.putString("duration", String.valueOf(_player.getDuration()));
      if (customSeek) {
        customSeek = false;
      } else {
        _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
          _playerView.getId(),
          "onSeek",
          map);
      }
    }
  };
  private final EventListener<PlayerEvent.Seeked> onSeekedListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onSeeked",
        map);
    }
  };
  private final EventListener<PlayerEvent.FullscreenEnter> onFullscreenEnterListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onFullscreenEnter",
        map);
    }
  };
  private final EventListener<PlayerEvent.FullscreenExit> onFullscreenExitListener = event -> {
    if(_player != null && _player.getSource() != null){
      WritableMap map = Arguments.createMap();
      _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        _playerView.getId(),
        "onFullscreenExit",
        map);
    }
  };
}
