has_wip_label = github.pr_labels.any? { |label| label.include? "Engineers at work" }
has_wip_title = github.pr_title.include? "[WIP]"
if has_wip_label || has_wip_title
  warn("PR is marked as Work in Progress")
end

warn("Big PR") if git.lines_of_code > 5000

module_dirs = {}
File.open("settings.gradle.kts", "r") do |file|
  file.each_line do |line|
    line = line.strip
    if line =~ /^project\(":(.*?)"\)\.projectDir\s*=\s*file\("([^"]+)"\)/
      module_dirs[$1] = $2
    end
  end
end

File.open("settings.gradle.kts", "r") do |file_handle|
  file_handle.each_line do |line|
    line = line.strip
    if line.start_with?("include(")
      match = line.match(/include\((.*)\)/)
      if match
        module_string = match[1].gsub(/["']/, '')
        gradleModule = module_string.gsub(":", "")
        if module_dirs.has_key?(gradleModule) && module_dirs[gradleModule].include?("sample")
          next
        end
        detektFile = "#{gradleModule}/build/reports/detekt/detekt.xml"
        if File.file?(detektFile)
          kotlin_detekt.report_file = detektFile
          kotlin_detekt.skip_gradle_task = true
          kotlin_detekt.severity = "warning"
          kotlin_detekt.filtering = true
          kotlin_detekt.detekt(inline_mode: true)
        else
          warn("No Detekt report found in #{detektFile} for module #{gradleModule}")
        end
      else
        warn("Could not parse module name from line: #{line}")
      end
    end
  end
end
