$files = git ls-files --others --exclude-standard
$counter = 1

Write-Host "Found $($files.Count) untracked files to commit."

foreach ($file in $files) {
    if (-Not (Test-Path -Path $file)) { continue }

    $issueKey = "STRM-FS-$($counter.ToString('000'))"
    $branchName = "feature/$issueKey"
    
    Write-Host "`n[$counter/$($files.Count)] Processing $file..."

    git checkout -b $branchName
    git add "$file"

    $filename = Split-Path $file -Leaf
    $commitMsg = "feat($issueKey): implement $filename"
    git commit -m $commitMsg

    git push -u origin $branchName

    git checkout main
    $mergeMsg = "Merge pull request #$counter from Achraf622-cpu/$branchName"
    git merge --no-ff $branchName -m "$mergeMsg"

    git push origin main
    git branch -d $branchName

    $counter++
}

Write-Host "`nAll frontend and backend files have been pushed incrementally! 🎉"
