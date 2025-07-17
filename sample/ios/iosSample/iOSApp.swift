import SwiftUI
import shared

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    private let testDataInitializer: TestDataInitializer

    init() {
        let context: PlatformContext = PlatformContextKt.getPlatformContext()
        self.testDataInitializer = TestDataInitializer(context: context)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
