for branch in `git branch -a | grep remotes | grep -v HEAD | grep -v master`; do
    git checkout --track $branch
    git rm --cached etc
    git commit -m "Removed submodule"
    git submodule add https://saurabh_aspl@bitbucket.org/saurabh_aspl/demo123/etc.git ./etc 
    git commit -m "Update submodule path"
    git push origin $branch
done
