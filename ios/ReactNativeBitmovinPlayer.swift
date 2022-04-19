import Foundation
import UIKit
import AVFoundation

@objc(ReactNativeBitmovinPlayer)
class ReactNativeBitmovinPlayer: RCTViewManager {
    var playerView: ViewController?
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        playerView?.multiply(a: a, b: b, resolve: resolve, reject: reject)
    }
    
//    @objc(withResolver:withRejecter:)
//    func isPiPAvailable(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//        playerView?.isPiPAvailable(resolve: resolve, reject: reject)
//    }


    override func view() -> UIView! {
        playerView = ViewController()
        return playerView;
    }

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc(play)
    func play() -> Void {
        playerView?.play()
    }

    @objc(pause)
    func pause() -> Void {
        playerView?.pause()
    }

    @objc(destroy)
    func destroy() -> Void {
        playerView?.destroy()
    }

    @objc(seekBackwardCommand)
    func seekBackwardCommand() -> Void {
        playerView?.seekBackwardCommand()
    }

    @objc(seekForwardCommand)
    func seekForwardCommand() -> Void {
        playerView?.seekForwardCommand()
    }
    
    @objc
    func isPiPAvailable(_ resolve:RCTPromiseResolveBlock, rejecter:RCTPromiseRejectBlock) -> Void {
        playerView?.isPiPAvailable(resolve, rejecter: rejecter)
    }

    @objc(enterPiP)
    func enterPiP() -> Void {
        playerView?.enterPiP()
    }

    @objc(exitPiP)
    func exitPiP() -> Void {
        playerView?.exitPiP()
    }

}
