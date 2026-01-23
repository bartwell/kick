import SwiftUI
import shared

enum DatabaseType: String, CaseIterable, Identifiable {
    case sqlDelight = "SqlDelight"
    case room = "Room"

    var id: String { self.rawValue }
}

struct ContentView: View {
    @State private var selectedTheme: AppTheme = .auto
    @State private var showButtonAlert = false
    @State private var isCollectingEvents = false
    @State private var controlPanelCollector: ControlPanelEventCollector?

    var body: some View {
        VStack(spacing: 20) {
            Picker("Select theme", selection: $selectedTheme) {
                ForEach(AppTheme.allCases) { theme in
                    Text(theme.rawValue).tag(theme)
                }
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding()

            Button("Launch viewer") {
                KickKt.shared.launch(context: PlatformContextKt.getPlatformContext())
            }
            .buttonStyle(.borderedProminent)
            .padding()
        }
        .padding()
        .onChange(of: selectedTheme) { newTheme in
            KickKt.shared.theme = newTheme.toLibraryTheme()
        }
        .preferredColorScheme(colorScheme(for: selectedTheme))
        .onAppear {
            KickKt.shared.theme = selectedTheme.toLibraryTheme()
            startControlPanelEventCollection()
        }
        .alert("You clicked the button", isPresented: $showButtonAlert) {
            Button("OK", role: .cancel) { }
        }
    }

    private func startControlPanelEventCollection() {
        guard !isCollectingEvents else { return }
        isCollectingEvents = true
        let collector = ControlPanelEventCollector { event in
            print("Control panel event: \(event)")
            if let clicked = event as? ControlPanelEvent.ButtonClicked,
               clicked.id == "show_alert" {
                KickKt.shared.close()
                showButtonAlert = true
            }
        }
        controlPanelCollector = collector
        KickCompanion.shared.controlPanel.event.collect(collector: collector) { error in
            if let error = error {
                print("Control panel event collection error: \(error)")
            }
        }
    }

    private func colorScheme(for theme: AppTheme) -> ColorScheme? {
        switch theme {
        case .auto:
            return nil
        case .dark:
            return .dark
        case .light:
            return .light
        }
    }
}

final class ControlPanelEventCollector: Kotlinx_coroutines_coreFlowCollector {
    private let onEvent: (Any?) -> Void

    init(onEvent: @escaping (Any?) -> Void) {
        self.onEvent = onEvent
    }

    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        onEvent(value)
        completionHandler(nil)
    }
}
