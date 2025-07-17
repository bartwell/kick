import SwiftUI
import shared

enum DatabaseType: String, CaseIterable, Identifiable {
    case sqlDelight = "SqlDelight"
    case room = "Room"
    
    var id: String { self.rawValue }
}

struct ContentView: View {
    @State private var selectedTheme: AppTheme = .auto
    
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
