package com.takeoffmediareactnativebitmovinplayer;

import android.view.View;

import com.bitmovin.player.PlayerView;
import com.bitmovin.player.api.ui.ScalingMode;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class ReactNativeBitmovinPlayerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext _reactContext;

  public ReactNativeBitmovinPlayerModule(ReactApplicationContext reactContext) {
    super(reactContext);

    _reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "ReactNativeBitmovinPlayer";
  }

  @ReactMethod
  public void play(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().play();
    } else {
      throw new ClassCastException(String.format("Cannot play: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void pause(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().pause();
    } else {
      throw new ClassCastException(String.format("Cannot pause: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void seek(int tag, double time) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().seek(time);
    } else {
      throw new ClassCastException(String.format("Cannot seek: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void mute(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().mute();
    } else {
      throw new ClassCastException(String.format("Cannot mute: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void unmute(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().unmute();
    } else {
      throw new ClassCastException(String.format("Cannot unmute: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void enterFullscreen(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).enterFullscreen();
    } else {
      throw new ClassCastException(String.format("Cannot enterFullscreen: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void exitFullscreen(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).exitFullscreen();
    } else {
      throw new ClassCastException(String.format("Cannot exitFullscreen: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void enterPiP(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);
    if (playerView instanceof  PlayerView) {
      ((PlayerView) playerView).enterPictureInPicture();
    } else {
      throw new ClassCastException(String.format("Cannot enter in PictureInPicture Mode: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public  void exitPiP(int tag) {
    View playerView = getCurrentActivity().findViewById(tag);
    if (playerView instanceof  PlayerView) {
      ((PlayerView) playerView).exitPictureInPicture();
    } else {
      throw new ClassCastException(String.format("Cannot exit from PictureInPicture Mode: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }


  @ReactMethod
  public void getCurrentTime(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      double currentTime = ((PlayerView) playerView).getPlayer().getCurrentTime();

      promise.resolve(currentTime);
    } else {
      throw new ClassCastException(String.format("Cannot getCurrentTime: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void getDuration(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      double duration = ((PlayerView) playerView).getPlayer().getDuration();

      promise.resolve(duration);
    } else {
      throw new ClassCastException(String.format("Cannot getDuration: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void getVolume(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      int volume = ((PlayerView) playerView).getPlayer().getVolume();

      promise.resolve(volume);
    } else {
      throw new ClassCastException(String.format("Cannot getVolume: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void setVolume(int tag, int volume) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().setVolume(volume);
    } else {
      throw new ClassCastException(String.format("Cannot setVolume: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void isMuted(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      boolean isMuted = ((PlayerView) playerView).getPlayer().isMuted();

      promise.resolve(isMuted);
    } else {
      throw new ClassCastException(String.format("Cannot isMuted: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void isPaused(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      boolean isPaused = ((PlayerView) playerView).getPlayer().isPaused();

      promise.resolve(isPaused);
    } else {
      throw new ClassCastException(String.format("Cannot isPaused: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void isStalled(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      boolean isStalled = ((PlayerView) playerView).getPlayer().isStalled();

      promise.resolve(isStalled);
    } else {
      throw new ClassCastException(String.format("Cannot isStalled: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void isPlaying(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      boolean isPlaying = ((PlayerView) playerView).getPlayer().isPlaying();

      promise.resolve(isPlaying);
    } else {
      throw new ClassCastException(String.format("Cannot isPlaying: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void setZoom(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).setScalingMode(ScalingMode.Zoom);

      promise.resolve(true);
    } else {
      throw new ClassCastException(String.format("Cannot setZoom: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void setFit(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).setScalingMode(ScalingMode.Fit);

      promise.resolve(true);
    } else {
      throw new ClassCastException(String.format("Cannot setFit: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }

  @ReactMethod
  public void destroy(int tag, Promise promise) {
    View playerView = getCurrentActivity().findViewById(tag);

    if (playerView instanceof PlayerView) {
      ((PlayerView) playerView).getPlayer().destroy();
    } else {
      throw new ClassCastException(String.format("Cannot destroy: view with tag #%d is not a ReactNativeBitmovinPlayer", tag));
    }
  }
}
