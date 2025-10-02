require "danger"

skip_detekt = ENV["SKIP_DETEKT"] == "1"
skip_junit  = ENV["SKIP_JUNIT"]  == "1"

begin
  require "danger-kotlin_detekt" unless skip_detekt
rescue LoadError
  warn("danger-kotlin_detekt not available")
end

begin
  require "danger-junit" unless skip_junit
rescue LoadError
  warn("danger-junit not available")
end

has_wip_label = github.pr_labels.any? { |label| label.include? "Engineers at work" }
has_wip_title = github.pr_title.include? "[WIP]"
warn("PR is marked as Work in Progress") if has_wip_label || has_wip_title
warn("Big PR") if git.lines_of_code > 5000

unless skip_detekt
  module_dirs = {}
  if File.exist?("settings.gradle.kts")
    File.foreach("settings.gradle.kts") do |line|
      line = line.strip
      if line =~ /^project\(":(.*?)"\)\.projectDir\s*=\s*file\("([^"]+)"\)/
        module_dirs[$1] = $2
      end
    end
    File.foreach("settings.gradle.kts") do |line|
      line = line.strip
      next unless line.start_with?("include(")
      modules = line.scan(/['\"]:(.*?)['\"]/).flatten
      modules.each do |mod_name|
        next if module_dirs[mod_name]&.include?("sample")
        base_dir = module_dirs.fetch(mod_name, mod_name)
        detekt_file = File.join(base_dir, "build", "reports", "detekt", "detekt.xml")
        if File.file?(detekt_file)
          kotlin_detekt.report_file = detekt_file
          kotlin_detekt.skip_gradle_task = true
          kotlin_detekt.severity = "warning"
          kotlin_detekt.filtering = true
          kotlin_detekt.detekt(inline_mode: true)
        else
          warn("No Detekt report found in #{detekt_file} for module #{mod_name}")
        end
      end
    end
  else
    warn("settings.gradle.kts not found for Detekt scan")
  end
end

unless skip_junit
  junit = Danger::DangerJunit.new(dangerfile: self)
  files = Dir["test-results/**/*.xml"]
  if files.empty?
    warn("No JUnit reports found")
  else
    junit.parse_files(files)
    junit.report
  end
end
