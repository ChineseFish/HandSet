#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "CDVBridge.h"

@implementation CDVBridge

+ (UIWindow *)fetchUIWindow {
    UIWindow *window = nil;
    
    if (@available(iOS 13.0, *))
    {
        for (UIWindowScene* windowScene in [UIApplication sharedApplication].connectedScenes)
        {
            if (windowScene.activationState == UISceneActivationStateForegroundActive)
            {
                window = windowScene.windows.firstObject;

                break;
            }
        }
    }
    else
    {
        window = [UIApplication sharedApplication].keyWindow;
    }

    return window;
}

- (void)jump:(CDVInvokedUrlCommand *)command {
    NSArray* args = command.arguments;

    //
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:1];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
