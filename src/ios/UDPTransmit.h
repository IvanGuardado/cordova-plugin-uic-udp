
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AudioToolbox/AudioServices.h>
#import <Cordova/CDVPlugin.h>

@interface UDPTransmit : CDVPlugin
	
	- (void)alert:(CDVInvokedUrlCommand*)command;
	
	@end

@interface CDVAlertView : UIAlertView {}
	
	@property (nonatomic, copy) NSString* callbackId;
	
	@end


