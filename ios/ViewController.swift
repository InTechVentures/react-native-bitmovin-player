//
//  ViewController.swift
//  ReactNativeBitmovinPlayer
//
//  Created by Jonathan Machado on 12/21/20.
//  Copyright © 2020 Facebook. All rights reserved.
//
import UIKit
import BitmovinPlayer
import BitmovinAnalyticsCollector

final class ViewController: UIView {
    @objc var autoPlay: Bool = false
    @objc var hasZoom: Bool = false
    @objc var configuration: NSDictionary? = nil
    @objc var analytics: NSDictionary? = nil

    var analyticsCollector: BitmovinPlayerCollector? = nil
    var player: Player?
    var nextCallback: Bool = false
    var customSeek: Bool = false
    var zoom: Bool = false
    var offset: TimeInterval = 0
    var hearbeat: Int = 10
    var drm: NSDictionary? = nil

    fileprivate var customMessageHandler: CustomMessageHandler?
    // Create player configuration
    let config = PlayerConfiguration()

    deinit {
        player?.destroy()
    }

    override func didSetProps(_ changedProps: [String]!) {

        try! config.setSourceItem(url: URL.init(string: self.configuration!["url"] as! String)!)
        if((self.configuration!["poster"]) != nil) {
            config.sourceItem?.posterSource = URL.init(string: self.configuration!["poster"] as! String)!
        }
        if((self.configuration!["subtitles"]) != nil) {
            let subtitleTrack = SubtitleTrack(url: URL(string: self.configuration!["subtitles"] as! String),
              label: "en",
              identifier: "en",
              isDefaultTrack: false,
              language: "en")
            config.sourceItem?.add(subtitleTrack: subtitleTrack)
        }
        if((self.configuration!["thumbnails"]) != nil) {
            let thumbnailsTrack = ThumbnailTrack(url: URL(string: self.configuration!["thumbnails"] as! String)!, label: "thumbnails", identifier: "thumbnails", isDefaultTrack: true)
            config.sourceItem?.thumbnailTrack = thumbnailsTrack;
        }

        if((self.configuration!["startOffset"]) != nil) {
            config.sourceItem?.options = SourceOptions();
            config.sourceItem?.options?.startOffset = self.configuration!["startOffset"] as! TimeInterval;
        }

        if((self.configuration!["hearbeat"]) != nil) {
            hearbeat = self.configuration!["hearbeat"] as! Int
        }

        if((self.configuration!["title"]) != nil) {
            config.sourceItem?.itemTitle = self.configuration!["title"] as? String;
        }

        if((self.configuration!["hasNextEpisode"]) != nil) {
            config.sourceItem?.metadata.addEntries(from: ["hasNextEpisode": self.configuration!["hasNextEpisode"] as! Bool])
        }

        if((self.configuration!["advisory"]) != nil) {
            config.sourceItem?.metadata.addEntries(from: ["advisory": self.configuration!["advisory"] as Any])
        }

        if((self.configuration!["subtitle"]) != nil) {
            config.sourceItem?.itemDescription = self.configuration!["subtitle"] as? String;
        }

        if (self.hasZoom == true){
            // config.sourceItem?.metadata.addEntries(from: ["hasZoom": self.hasZoom])
            config.styleConfiguration.scalingMode = BMPScalingMode.zoom;
        }

        if (self.autoPlay == true){
            config.playbackConfiguration.isAutoplayEnabled = true;
        }

        if((self.configuration!["drm"]) != nil) {
            drm = self.configuration!["drm"] as? NSDictionary;
            if (drm?.value(forKey: "isDrmEn") as! Bool == true) {
                let licenseURL = URL.init(string: (drm?.value(forKey: "licenseUrl") as! String))
                let certificateURL = URL.init(string: (drm?.value(forKey: "certificateUrl") as! String))
                // create drm configuration
                let fpsConfig = FairplayConfiguration(license: licenseURL!, certificateURL: certificateURL!)
                // Example of how certificate data can be prepared if custom modifications are needed
                fpsConfig.prepareCertificate = { (data: Data) -> Data in
                    // Do something with the loaded certificate
                    return data
                }
                fpsConfig.licenseRequestHeaders = [
                    "content-type": "application/octet-stream",
                    (drm?.value(forKey: "header") as! String): (drm?.value(forKey: "token") as! String)
                ]

                fpsConfig.prepareMessage = { spcData, assetId in
                    spcData
                }
                fpsConfig.prepareLicense = { ckc in
                     ckc
                }

                fpsConfig.prepareContentId = { contentId in
                    let ret = contentId.components(separatedBy: "/").last ?? contentId
                    return ret
                }

                fpsConfig.prepareSyncMessage = { spcData, assetId in
                    spcData
                }
                config.sourceItem?.add(drmConfiguration: fpsConfig)
            }
        }

        player?.setup(configuration: config)
        nextCallback = false;

        if(self.analytics != nil) {
            var plistDictionary: NSDictionary?
            if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
                plistDictionary = NSDictionary(contentsOfFile: path)
            }
            // Create a BitmovinAnalyticsConfig using your Bitmovin analytics license key and/or your Bitmovin Player Key
            let configAnalytics:BitmovinAnalyticsConfig = BitmovinAnalyticsConfig(key: plistDictionary!["BitmovinAnalyticsLicenseKey"] as! String, playerKey: plistDictionary!["BitmovinPlayerLicenseKey"] as! String)

            configAnalytics.videoId = self.analytics!["videoId"] as? String;
            configAnalytics.title = self.analytics!["title"] as? String;
            configAnalytics.customerUserId = self.analytics!["userId"] as? String;
            configAnalytics.cdnProvider = self.analytics!["cdnProvider"] as? String;
            configAnalytics.customData1 = self.analytics!["customData1"] as? String;
            configAnalytics.customData2 = self.analytics!["customData2"] as? String;
            configAnalytics.customData3 = self.analytics!["customData3"] as? String;
            configAnalytics.customData4 = self.analytics!["customData4"] as? String;

            // Create a BitmovinAnalytics object using the config just created
            analyticsCollector = BitmovinAnalytics(config: configAnalytics);

            // Attach your player instance
            analyticsCollector!.attachPlayer(player: player!);
        }

    }

    override init(frame:CGRect) {
        super.init(frame: frame)

        var plistDictionary: NSDictionary?
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            plistDictionary = NSDictionary(contentsOfFile: path)
        }

        /**
         * Go to https://github.com/bitmovin/bitmovin-player-ui to get started with creating a custom player UI.
         */
        if (plistDictionary!["BitmovinPlayerCss"] != nil) {
            config.styleConfiguration.playerUiCss = URL(string: plistDictionary!["BitmovinPlayerCss"] as! String)!
        }
        if (plistDictionary!["BitmovinPlayerJs"] != nil) {
            config.styleConfiguration.playerUiJs = URL(string: plistDictionary!["BitmovinPlayerJs"] as! String)!
        }

        config.styleConfiguration.userInterfaceConfiguration = bitmovinUserInterfaceConfiguration

        // Create player based on player configuration
        player = Player(configuration: config)

        // Create player view and pass the player instance to it
        let playerView = BMPBitmovinPlayerView(player: player!, frame: .zero)

        // Listen to player events
        player!.add(listener: self)

        playerView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        playerView.frame = frame

        playerView.add(listener: self)

        // Make sure that the correct audio session category is set to allow for background playback.
        handleAudioSessionCategorySetting()

        self.addSubview(playerView)
    }

    @objc var onReady:RCTDirectEventBlock? = nil
    @objc var onAirPlay:RCTDirectEventBlock? = nil
    @objc var onPlay:RCTDirectEventBlock? = nil
    @objc var onPause:RCTDirectEventBlock? = nil
    @objc var onEvent:RCTDirectEventBlock? = nil
    @objc var onError:RCTDirectEventBlock? = nil
    @objc var onSeek:RCTDirectEventBlock? = nil
    @objc var onForward:RCTDirectEventBlock? = nil
    @objc var onRewind:RCTDirectEventBlock? = nil

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    @IBAction fileprivate func toggleCloseButton(_ sender: Any) {
        // Use the configured customMessageHandler to send messages to the UI
        customMessageHandler?.sendMessage("toggleCloseButton")
    }

    @IBAction fileprivate func toggleForwardButton(_ sender: Any) {
        // Use the configured customMessageHandler to send messages to the UI
        customMessageHandler?.sendMessage("toggleForwardButton")
    }

    @IBAction fileprivate func toggleRewindButton(_ sender: Any) {
        // Use the configured customMessageHandler to send messages to the UI
        customMessageHandler?.sendMessage("toggleRewindButton")
    }

    @IBAction fileprivate func nextEpisodeButton(_ sender: Any) {
        // Use the configured customMessageHandler to send messages to the UI
        customMessageHandler?.sendMessage("nextEpisodeButton")
    }

    fileprivate var bitmovinUserInterfaceConfiguration: BitmovinUserInterfaceConfiguration {
        // Configure the JS <> Native communication
        let bitmovinUserInterfaceConfiguration = BitmovinUserInterfaceConfiguration()
        // Create an instance of the custom message handler
        customMessageHandler = CustomMessageHandler()
        customMessageHandler?.delegate = self
        bitmovinUserInterfaceConfiguration.customMessageHandler = customMessageHandler
        return bitmovinUserInterfaceConfiguration
    }

    func play() -> Void {
        DispatchQueue.main.async { [unowned self] in
            player?.play()
        }
    }

    func seekBackwardCommand() -> Void {
        DispatchQueue.main.async { [unowned self] in
            player?.seek(time: self.player!.currentTime - 10)
        }
    }

    func seekForwardCommand() -> Void {
        DispatchQueue.main.async { [unowned self] in
            player?.seek(time: self.player!.currentTime + 10)
        }
    }

    func pause() -> Void {
        DispatchQueue.main.async { [unowned self] in
            player?.pause()
        }
    }

    func destroy() -> Void {
        DispatchQueue.main.async { [unowned self] in
            player?.destroy()
        }
    }

    func handleAudioSessionCategorySetting() {
        let audioSession = AVAudioSession.sharedInstance()

        // When AVAudioSessionCategoryPlayback is already active, we have nothing to do here
        guard audioSession.category.rawValue != AVAudioSession.Category.playback.rawValue else { return }

        do {
            try audioSession.setCategory(AVAudioSession.Category.playback, mode: AVAudioSession.Mode.moviePlayback)
        } catch {
            print("Setting category to AVAudioSessionCategoryPlayback failed.")
        }
    }

}

// MARK: - CustomMessageHandlerDelegate
extension ViewController: CustomMessageHandlerDelegate {
    func receivedSynchronousMessage(_ message: String, withData data: String?) -> String? {
        print("onEvent =) \(message)")

        if (message == "closePlayer") {
            DispatchQueue.main.async { [unowned self] in
                player?.destroy()
                // Detach your player when you are done.
                if (analyticsCollector != nil) {
                    analyticsCollector!.detachPlayer()
                }
            }
        }

        if (message == "nextEpisode") {
            DispatchQueue.main.async { [unowned self] in
                player?.destroy()
                // Detach your player when you are done.
                if (analyticsCollector != nil) {
                    analyticsCollector!.detachPlayer()
                }
            }
        }

        if (message == "forwardButton") {
            if((self.onForward) != nil) {
                self.onForward!(["message": message, "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
                customSeek = true;
            }
            player?.seek(time: self.player!.currentTime + 10)
        }

        if (message == "rewindButton") {
            if((self.onRewind) != nil) {
                self.onRewind!(["message": message, "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
                customSeek = true;
            }
            player?.seek(time: self.player!.currentTime - 10)
        }

        if (message == "zoomButton") {
            zoom = !zoom;
            config.styleConfiguration.scalingMode = zoom ? BMPScalingMode.zoom : BMPScalingMode.fit;
        }


        if((self.onEvent) != nil) {
            self.onEvent!(["message": message, "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }

        return nil
    }

    func receivedAsynchronousMessage(_ message: String, withData data: String?) {
        print("received Asynchronouse Messagse", message, data ?? "")
    }
}

extension ViewController: UserInterfaceListener {
    func onControlsHide(_ event: ControlsHideEvent) {
        print("onControlsHide")
    }

    func onControlsShow(_ event: ControlsShowEvent) {
        print("onControlsShow")
    }
}

extension ViewController: PlayerListener {

    func onPlay(_ event: PlayEvent) {
        print("onPlay \(event.name)")
        let playCallback = self.onPlay;
        if((playCallback) != nil && self.player!.duration > 0) {
            playCallback!(["message": "play", "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }
    }

    func onAirPlayChanged(_ event: AirPlayChangedEvent) {
        if((self.onAirPlay) != nil) {
            self.onAirPlay!(["message": "onAirPlay", "value": self.player?.isAirPlayActive ?? false])
        }
    }

    func onSeeked(_ event: SeekedEvent) {
        print("onSeeked \(event.name) \(event.timestamp) \(self.player?.currentTime as Any)")
        if (customSeek) {
            customSeek = false;
        } else if((self.onSeek) != nil) {
            self.onSeek!(["message": "seek", "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }
    }


    func onReady(_ event: ReadyEvent) {
        print("onReady \(event.name)")
        let readyCallback = self.onReady;
        if((readyCallback) != nil) {
            readyCallback!(["message": "load", "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }
    }

    func onPaused(_ event: PausedEvent) {
        print("onPaused \(event.time)")
        if((self.onPause) != nil) {
            self.onPause!(["message": "pause", "time": self.player?.currentTime as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }
    }

    func onTimeChanged(_ event: TimeChangedEvent) {
        print("onTimeChanged \(event.currentTime) \(self.player?.duration ?? 0)")
        if (event.currentTime <= (self.player?.duration ?? 0) - (self.configuration!["nextPlayback"] as! Double) && nextCallback) {
            nextCallback = false;
        }
        if (event.currentTime > (self.player?.duration ?? 0) - (self.configuration!["nextPlayback"] as! Double) && !nextCallback) {
            if((self.onEvent) != nil) {
                nextCallback = true;
                self.onEvent!(["message": "next"])
            }
        }

        if((event.currentTime > (offset + Double(hearbeat)) || event.currentTime < (offset - Double(hearbeat))) && event.currentTime < (self.player?.duration ?? 0)) {
            offset = event.currentTime;
            if((self.onEvent) != nil) {
                self.onEvent!(["message": "save", "time": offset as Any, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
            }
        }
    }

    func onDurationChanged(_ event: DurationChangedEvent) {
        print("onDurationChanged \(event.duration)")
    }

    func onError(_ event: ErrorEvent) {
        print("onError \(event.message)")
        let errorCallback = self.onError;
        if((errorCallback) != nil) {
            errorCallback!(["code": event.code, "message": event.message, "volume": self.player?.volume as Any, "duration": self.player?.duration as Any])
        }
    }
}
