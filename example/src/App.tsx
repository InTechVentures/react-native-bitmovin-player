import * as React from 'react';

import { Platform, StyleSheet, AppState as AppStateRN } from 'react-native';
import ReactNativeBitmovinPlayer, {
  ReactNativeBitmovinPlayerMethodsType,
} from '@takeoffmedia/react-native-bitmovin-player';
import { useEffect, useState } from 'react';

const videoUrl = Platform.select({
  ios:
    'https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8',
  // ios: 'https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8',
  android:
    'https://ftp.itec.aau.at/datasets/DASHDataset2014/BigBuckBunny/15sec/BigBuckBunny_15s_simple_2014_05_09.mpd',
  default:
    'https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8',
});

export default function App() {
  const playerRef = React.useRef<ReactNativeBitmovinPlayerMethodsType>();
  const [isInPipMode, setIsInPipMode] = useState(false);
  const [isPlaying, setisPlaying] = useState(false);
  const [isPipAvailable, setisPipAvailable] = useState(false);

  async function getIsPipAvailable() {
    try {
      if (playerRef.current) {
        const isPipAvl = (await playerRef.current.isPiPAvailable()) || false;
        setisPipAvailable(isPipAvl);
        console.log('isPipAvl: ', isPipAvl);
      }
    } catch (error) {
      console.log('isPipAvailable error:', error);
      setisPipAvailable(false);
    }
  }

  useEffect(() => {
    return () => {
      //
    };
  }, []);

  useEffect(() => {
    AppStateRN.addEventListener('change', (action) => {
      // console.log(isPlaying);
      if (action === 'background' && isPlaying && isPipAvailable) {
        if (playerRef.current) {
          playerRef.current.enterPiP();
        }
      }
    });

    return () => {
      AppStateRN.removeEventListener('change', () => {});
    };
  }, []);

  return (
    <ReactNativeBitmovinPlayer
      ref={playerRef as any}
      style={styles.container}
      autoPlay={false}
      hasZoom={false}
      inPiPMode={isInPipMode}
      configuration={{
        title: 'It works',
        subtitle: 'S1 · E1',
        startOffset: 10,
        nextPlayback: 30,
        hasNextEpisode: false,
        advisory: {
          classification: 'TV-PG',
          description: 'All Drama',
        },
        hearbeat: 10,
        url: videoUrl,
        poster:
          'https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Big.Buck.Bunny.-.Opening.Screen.png/800px-Big.Buck.Bunny.-.Opening.Screen.png',
        // subtitles:
        //   'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt',
        subtitles: [
          {
            label: 'English [CC]',
            language: 'en-US',
            href:
              'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt',
          },
          {
            label: 'English',
            language: 'en',
            href:
              'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_en.vtt',
          },
          {
            label: 'Deutsch',
            language: 'de',
            href:
              'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_de.vtt',
          },
          {
            label: 'Espanol',
            language: 'es',
            href:
              'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_es.vtt',
          },
          {
            label: 'Français',
            language: 'fr',
            href:
              'https://bitdash-a.akamaihd.net/content/sintel/subtitles/subtitles_fr.vtt',
          },
        ],
        thumbnails:
          'https://bitdash-a.akamaihd.net/content/sintel/sprite/sprite.vtt',
      }}
      onReady={({ nativeEvent }) => {
        console.log({ nativeEvent });
        getIsPipAvailable();
      }}
      onEvent={({ nativeEvent }) => {
        console.log({ nativeEvent });
      }}
      onPause={({ nativeEvent }) => {
        console.log({ nativeEvent });
        setisPlaying(false);
      }}
      onPlay={({ nativeEvent }) => {
        console.log({ nativeEvent });
        setisPlaying(true);
      }}
      onSeek={({ nativeEvent }) => {
        console.log({ nativeEvent });
      }}
      onForward={({ nativeEvent }) => {
        console.log({ nativeEvent });
      }}
      onRewind={({ nativeEvent }) => {
        console.log({ nativeEvent });
      }}
      onPipMode={({ nativeEvent }) => {
        setIsInPipMode(nativeEvent.value);
      }}
    />
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'black',
  },
});
