#import <Foundation/Foundation.h>
#import <MobileCoreServices/UTType.h>
#import "CDVURLProtocolFreeGo.h"

NSString *const kFreeGoCDVAssetsLibraryPrefixes = @"http://injection/";

@implementation CDVURLProtocolFreeGo

// 这个方法用来拦截H5页面请求
+ (BOOL)canInitWithRequest:(NSURLRequest *)theRequest
{
    NSURL *theUrl = [theRequest URL];

    // 判断是否是我们定义的url，若是，返回YES，继续执行其他方法，若不是，返回NO，不执行其他方法
    if ([[theUrl absoluteString] hasPrefix:kFreeGoCDVAssetsLibraryPrefixes]) {
        NSLog(@"CDVURLProtocolFreeGo canInitWithRequest, receive fetch cordova.js request");

        return YES;
    }

    return NO;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request
{
    NSLog(@"CDVURLProtocolFreeGo canonicalRequestForRequest, received %@", NSStringFromSelector(_cmd));

    return request;
}

// 获取cordova.js本地文件路径
- (NSString *)pathForResource:(NSString *)resourcepath
{
    NSBundle *mainBundle = [NSBundle mainBundle];
    NSMutableArray *directoryParts = [NSMutableArray arrayWithArray:[resourcepath componentsSeparatedByString:@"/"]];
    NSString *filename = [directoryParts lastObject];
    
    [directoryParts removeLastObject];
    NSString *directoryPartsJoined = [directoryParts componentsJoinedByString:@"/"];
    NSString *directoryStr = @"www";
    
    if ([directoryPartsJoined length] > 0) {
        directoryStr = [NSString stringWithFormat:@"%@/%@", directoryStr, [directoryParts componentsJoinedByString:@"/"]];
    }
    
    return [mainBundle pathForResource:filename ofType:@"" inDirectory:directoryStr];
}

// 在canInitWithRequest方法返回YES以后，会执行该方法，完成替换资源并返回给H5页面
- (void)startLoading
{
    NSLog(@"%@ CDVURLProtocolFreeGo startLoading, received %@", self, NSStringFromSelector(_cmd));

    NSString *url= super.request.URL.resourceSpecifier;
    NSString *cordova = [url stringByReplacingOccurrencesOfString:@"//injection/" withString:@""];
    NSURL *startURL = [NSURL URLWithString:cordova];

    // 获取cordova.js
    NSString *cordovaFilePath =[self pathForResource:[startURL path]];
    if (!cordovaFilePath) {
        NSLog(@"%@ CDVURLProtocolFreeGo startLoading, cordova.js not exist");

        // cordova.js没有找到
        [self sendResponseWithResponseCode:401 data:nil mimeType:nil]; //重要
        return;
    }

    // cordova.js存在
    CFStringRef pathExtension = (__bridge_retained CFStringRef)[cordovaFilePath pathExtension];
    CFStringRef type = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, pathExtension, NULL);
    CFRelease(pathExtension);
    NSString *mimeType = (__bridge_transfer NSString *)UTTypeCopyPreferredTagWithClass(type, kUTTagClassMIMEType);
    if (type != NULL)
    {
        CFRelease(type);
    }
        
    // 获取cordova.js文件的内容
    NSData *data = [NSData dataWithContentsOfFile:cordovaFilePath];

    // 返回cordova.js的内容
    [self sendResponseWithResponseCode:200 data:data mimeType:mimeType];
}


- (void)stopLoading
{
    // do any cleanup here
}

+ (BOOL)requestIsCacheEquivalent:(NSURLRequest *)requestA toRequest:(NSURLRequest *)requestB
{
    return NO;
}

// 将cordova.js返回给H5页面
- (void)sendResponseWithResponseCode:(NSInteger)statusCode data:(NSData *)data mimeType:(NSString *)mimeType
{
    if (mimeType == nil) {
        mimeType = @"text/plain";
    }

    NSHTTPURLResponse *response = [[NSHTTPURLResponse alloc] initWithURL:[[self request] URL] statusCode:statusCode HTTPVersion:@"HTTP/1.1" headerFields:@{@"Content-Type" : mimeType}];

    [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
    if (data != nil) {
        [[self client] URLProtocol:self didLoadData:data];
    }
    [[self client] URLProtocolDidFinishLoading:self];
}

@end
