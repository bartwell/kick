import shared

enum AppTheme: String, CaseIterable, Identifiable {
    case auto = "Auto"
    case dark = "Dark"
    case light = "Light"
    
    var id: String { rawValue }
    
    func toLibraryTheme() -> Theme {
        switch self {
        case .auto:
            return Theme.Auto()
        case .dark:
            return Theme.Dark()
        case .light:
            return Theme.Light()
        }
    }
}
