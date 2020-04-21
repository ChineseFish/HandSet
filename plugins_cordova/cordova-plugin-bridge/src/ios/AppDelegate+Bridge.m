
#import "AppDelegate+Bridge.h"
#import "MainViewController.h"
#import "CDVURLProtocolFreeGo.h"
#import <CCBNetPaySDK/CCBNetPay.h>

#import <objc/runtime.h>

@implementation AppDelegate (Bridge)

- (BOOL)application:(UIApplication *)application openURL:(nonnull NSURL *)url options:(nonnull NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options
{
    [[CCBNetPay defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *dic) {
        
    }];

    return YES;
}

- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    [NSURLProtocol registerClass:[CDVURLProtocolFreeGo class]];
    
    //
    self.viewController = [[MainViewController alloc] init];
    return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
