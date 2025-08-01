FLUTTER := flutter
FLUTTER_BUILD := $(FLUTTER) build

ANDROID_OUTPUT := build/app/outputs/flutter-apk/app-release.apk
DROPBOX_FOLDER := ~/Dropbox/Development/Build
PRODUCTION_ENV := --dart-define=ENV=production

ios-clean:
	flutter clean && flutter pub get && rm -rf ios/Pods && rm -f ios/Podfile.lock && (cd ios && pod install)

ios-release:
	$(FLUTTER_BUILD) ipa --export-method=ad-hoc

ios-production:
	$(FLUTTER_BUILD) ipa --export-method=app-store $(PRODUCTION_ENV)

android-release:
	$(FLUTTER_BUILD) apk
	cp $(ANDROID_OUTPUT) $(DROPBOX_FOLDER)
	
android-production:
	$(FLUTTER_BUILD) appbundle $(PRODUCTION_ENV)
