has_wip_label = github.pr_labels.any? { |label| label.include? "Engineers at work" }
has_wip_title = github.pr_title.include? "[WIP]"
if has_wip_label || has_wip_title
  warn("PR is marked as Work in Progress")
end

warn("Big PR") if git.lines_of_code > 5000

module_dirs = {}
File.foreach("settings.gradle.kts") do |line|
  line.strip!
  if line =~ /^project\(":(.*?)"\)\.projectDir\s*=\s*file\("([^"]+)"\)/
    module_dirs[$1] = $2
  end
end

File.foreach("settings.gradle.kts") do |line|
  line.strip!
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
