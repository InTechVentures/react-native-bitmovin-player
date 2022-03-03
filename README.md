# @takeoffmedia/react-native-bitmovin-player

#### Attention! This plugin is not official, the code is not provided or maintained by [Bitmovin](https://bitmovin.com)!

## Installation
```sh
npm install @takeoffmedia/react-native-bitmovin-player
```
## Project Setup

+ Add in your pod file:
  + `source 'https://github.com/bitmovin/cocoapod-specs.git'`
  + `pod 'BitmovinPlayer', '2.57.1'`

+   Add your Bitmovin player license key to the `Info.plist` file as `BitmovinPlayerLicenseKey`.

+   Add the Bundle identifier of the iOS application which is using the SDK as an allowed domain to the Bitmovin licensing backend. This can be also done under `Player -> Licenses` when logging in into [https://dashboard.bitmovin.com](https://dashboard.bitmovin.com) with your account.

    When you do not do this, you'll get a license error when starting the application which contains the player.

    Your player license key can be found when logging in into [https://dashboard.bitmovin.com](https://dashboard.bitmovin.com) and navigating to `Player -> Licenses`.


+ Optional: You can add a custom CSS and JS in `Info.plist`

    + Key to the `Info.plist` file as `BitmovinPlayerCss`.

    + Key to the `Info.plist` file as `BitmovinPlayerJs`.


#### Note for iOS 12
If you develop using XCode 10 targeting iOS 12 make sure you use a provisioning profile with `Access WiFi Information` enabled for the `BasicCasting` as well as for the `AdvancedCasting` sample.

#### Note for iOS 13
If you develop using XCode 11 targeting iOS 13 please make sure the `NSBluetoothAlwaysUsageDescription` key is set in the `info.plist` for both the `BasicCasting` and the `AdvancedCasting` samples.

## Usage

```js
import ReactNativeBitmovinPlayer from '@takeoffmedia/react-native-bitmovin-player';

<ReactNativeBitmovinPlayer
  autoPlay={false}
  configuration={{
    url: '',
    poster: '',
    subtitles: '',
    thumbnails: '',
    startOffset: 0
  }}
/>

```
## Configuration

// TODO
## Events

// TODO
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
