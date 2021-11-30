import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';

import {
  findNodeHandle,
  NativeModules,
  requireNativeComponent,
  Platform,
  View,
  LayoutRectangle,
} from 'react-native';

const ReactNativeBitmovinPlayerModule = NativeModules.ReactNativeBitmovinPlayer;

const DEFAULT_CONFIGURATION = {
  style: {
    uiEnabled: true,
    fullscreenIcon: true,
  },
};

export type ReactNativeBitmovinPlayerMethodsType = {
  play(): void;
  pause(): void;
  destroy(): void;
  enterPiP(): void;
  exitPiP(): void;
  seekBackwardCommand(): void;
  seekForwardCommand(): void;
};

type ReactNativeBitmovinPlayerType = {
  autoPlay: boolean;
  hasZoom: boolean;
  inPiPMode?: boolean;
  hasChromecast?: boolean;
  style?: any;
  color?: string;
  onReady?: (event: any) => void;
  onChromecast?: (event: any) => void;
  onPlay?: (event: any) => void;
  onAirPlay?: (event: any) => void;
  onPause?: (event: any) => void;
  onEvent?: (event: any) => void;
  onError?: (event: any) => void;
  onSeek?: (event: any) => void;
  onForward?: (event: any) => void;
  onRewind?: (event: any) => void;
  onTimeChanged?: (event: any) => void;
  onStallStarted?: (event: any) => void;
  onStallEnded?: (event: any) => void;
  onPlaybackFinished?: (event: any) => void;
  onRenderFirstFrame?: (event: any) => void;
  onPlayerError?: (event: any) => void;
  onMuted?: (event: any) => void;
  onUnmuted?: (event: any) => void;
  onSeeked?: (event: any) => void;
  onFullscreenEnter?: (event: any) => void;
  onFullscreenExit?: (event: any) => void;
  onControlsShow?: (event: any) => void;
  onControlsHide?: (event: any) => void;
  onPipMode?: (event: any) => void;
  configuration: {
    url: string;
    poster?: string;
    startOffset: number;
    hasNextEpisode: boolean;
    subtitles?: string;
    thumbnails?: string;
    title?: string;
    subtitle?: string;
    nextPlayback?: number;
    hearbeat?: number;
    advisory?: {
      classification: string;
      description: string;
    };
  };
  analytics?: {
    videoId: string;
    title?: string;
    userId?: string;
    cdnProvider: string;
    customData1: string;
    customData2: string;
    customData3: string;
    customData4: string;
  };
};

const ReactNativeBitmovinPlayer = requireNativeComponent<ReactNativeBitmovinPlayerType>(
  'ReactNativeBitmovinPlayer'
);

export default React.forwardRef<
  ReactNativeBitmovinPlayerMethodsType,
  ReactNativeBitmovinPlayerType
>(
  (
    {
      configuration,
      onReady,
      hasZoom,
      hasChromecast,
      inPiPMode,
      autoPlay,
      style,
      ...props
    }: ReactNativeBitmovinPlayerType,
    ref
  ) => {
    const [maxHeight, setMaxHeight] = useState<number | null>(null);
    const [layout, setLayout] = useState<LayoutRectangle | null>(null);
    const [loading, setLoading] = useState(false);
    const playerRef = useRef();

    useEffect(() => {
      if (Platform.OS === 'android') {
        // Make native module listen for lifecycle events on the main activity.
        ReactNativeBitmovinPlayerModule.registerLifecycleEventObserver();
      }
    }, []);

    useEffect(() => {
      const { height } = layout || {};

      if (maxHeight !== null && height && !loading) {
        setTimeout(() => {
          setMaxHeight(height);
        }, 300);
      }

      if (height && maxHeight === height) {
        setLoading(true);
      }
    }, [maxHeight, layout, loading]);

    useEffect(() => {
      if (loading && autoPlay && Platform.OS === 'android') {
        play();
      }
    }, [loading, autoPlay]);

    const play = () => {
      if (Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.play(
          findNodeHandle(playerRef?.current || null)
        );
      } else {
        ReactNativeBitmovinPlayerModule.play();
      }
    };

    const pause = () => {
      if (Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.pause(
          findNodeHandle(playerRef?.current || null)
        );
      } else {
        ReactNativeBitmovinPlayerModule.pause();
      }
    };

    const seekBackwardCommand = () => {
      ReactNativeBitmovinPlayerModule.seekBackwardCommand();
    };

    const seekForwardCommand = () => {
      ReactNativeBitmovinPlayerModule.seekForwardCommand();
    };

    const destroy = () => {
      if (Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.destroy(
          findNodeHandle(playerRef.current || null)
        );
      } else {
        ReactNativeBitmovinPlayerModule.destroy();
      }
    };

    const setZoom = () => {
      ReactNativeBitmovinPlayerModule.setZoom(
        findNodeHandle(playerRef.current || null)
      );
    };

    const setFit = () => {
      ReactNativeBitmovinPlayerModule.setFit(
        findNodeHandle(playerRef.current || null)
      );
    };

    const seek = (time = 0) => {
      const seekTime = parseFloat(time.toString());

      if (seekTime) {
        ReactNativeBitmovinPlayerModule.seek(
          findNodeHandle(playerRef.current || null),
          seekTime
        );
      }
    };

    const mute = () => {
      ReactNativeBitmovinPlayerModule.mute(
        findNodeHandle(playerRef.current || null)
      );
    };

    const unmute = () => {
      ReactNativeBitmovinPlayerModule.unmute(
        findNodeHandle(playerRef.current || null)
      );
    };

    const enterFullscreen = () => {
      ReactNativeBitmovinPlayerModule.enterFullscreen(
        findNodeHandle(playerRef.current || null)
      );
    };

    const exitFullscreen = () => {
      ReactNativeBitmovinPlayerModule.exitFullscreen(
        findNodeHandle(playerRef.current || null)
      );
    };

    const getCurrentTime = () =>
      ReactNativeBitmovinPlayerModule.getCurrentTime(
        findNodeHandle(playerRef.current || null)
      );

    const getDuration = () =>
      ReactNativeBitmovinPlayerModule.getDuration(
        findNodeHandle(playerRef.current || null)
      );

    const getVolume = () =>
      ReactNativeBitmovinPlayerModule.getVolume(
        findNodeHandle(playerRef.current || null)
      );

    const setVolume = (volume = 100) => {
      ReactNativeBitmovinPlayerModule.setVolume(
        findNodeHandle(playerRef.current || null),
        volume
      );
    };

    const isMuted = () =>
      ReactNativeBitmovinPlayerModule.isMuted(
        findNodeHandle(playerRef.current || null)
      );

    const isPaused = () =>
      ReactNativeBitmovinPlayerModule.isPaused(
        findNodeHandle(playerRef.current || null)
      );

    const isStalled = () =>
      ReactNativeBitmovinPlayerModule.isStalled(
        findNodeHandle(playerRef.current || null)
      );

    const isPlaying = () =>
      ReactNativeBitmovinPlayerModule.isPlaying(
        findNodeHandle(playerRef.current || null)
      );

    const enterPiP = () => {
      if (Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.enterPiP(
          findNodeHandle(playerRef.current || null)
        );
      } else {
        ReactNativeBitmovinPlayerModule.enterPiP();
      }
    };

    const exitPiP = () => {
      if (Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.exitPiP(
          findNodeHandle(playerRef.current || null)
        );
      } else {
        ReactNativeBitmovinPlayerModule.exitPiP();
      }
    };

    useImperativeHandle(ref, () => ({
      play,
      pause,
      seekBackwardCommand,
      seekForwardCommand,
      destroy,
      setZoom,
      setFit,
      seek,
      mute,
      unmute,
      enterFullscreen,
      exitFullscreen,
      getCurrentTime,
      getDuration,
      getVolume,
      setVolume,
      isMuted,
      isPaused,
      isStalled,
      isPlaying,
      enterPiP,
      exitPiP,
    }));

    const _onReady = (event: any) => {
      // this need because video view stretched on initial render (RN 0.55.4)
      // TODO: check in future releases of RN
      if (Platform.OS === 'android') {
        if (layout && maxHeight === null) {
          const { height } = layout;
          setMaxHeight(height - 1);
        }
      }
      if (hasZoom && Platform.OS === 'android') {
        ReactNativeBitmovinPlayerModule.setZoom(
          findNodeHandle(playerRef.current || null)
        );
      }

      if (onReady) {
        onReady(event);
      }
    };

    return (
      <View
        style={{ flex: 1 }}
        onLayout={(event) => {
          setLayout(event?.nativeEvent?.layout || null);
        }}
      >
        <ReactNativeBitmovinPlayer
          ref={playerRef as any}
          {...{
            autoPlay,
            hasZoom,
            hasChromecast,
            inPiPMode,
            configuration: {
              ...DEFAULT_CONFIGURATION,
              ...configuration,
            },
            ...props,
          }}
          onReady={_onReady}
          style={[
            maxHeight
              ? {
                  maxHeight,
                }
              : null,
            style,
            loading || Platform.OS === 'ios'
              ? {}
              : {
                  opacity: 0,
                },
          ]}
        />
      </View>
    );
  }
);
