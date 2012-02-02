echo "What is the commit reason? Please enter it below...."
read commit
git init
git commit -a -m "$commit"
git push
